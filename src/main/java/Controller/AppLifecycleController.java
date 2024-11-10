package Controller;

import Services.CountExchangeService;
import Services.CurrencyService;
import Services.ExchangeRateService;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@WebListener
public class AppLifecycleController implements ServletContextListener {
    private static final String URL = "jdbc:sqlite:../webapps/CurrencyExchanger-1.0/WEB-INF/classes/database.db";
    private List<Connection> realConnections = new ArrayList<>();
    BlockingQueue<Connection> connectionPool = new ArrayBlockingQueue<>(10);
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        // use a logger library instead of system.out.println, e.g. sl4fj
        System.out.println("________________________________________________________________");
        createConnectionPool();
        CountExchangeService countExchangeService = new CountExchangeService(connectionPool,URL);
        CurrencyService currencyService = new CurrencyService(connectionPool,URL);
        ExchangeRateService exchangeRateService = new ExchangeRateService(connectionPool,URL);

        ServletContext context = servletContextEvent.getServletContext();
        context.setAttribute("countExchangeService",countExchangeService);
        context.setAttribute("currencyService",currencyService);
        context.setAttribute("exchangeRateService",exchangeRateService);
    }

    private void createConnectionPool(){
        try {
            //старая версия
            Class.forName("org.sqlite.JDBC");
            Class clazz = Connection.class;

            // in real life this connection pooling lib is usually used https://www.baeldung.com/hikaricp

            for(int i = 0 ; i<10; i++){ // 10 is a magic number you use twice. Create a constant
                Connection conn = DriverManager.getConnection(URL);
                realConnections.add(conn);

                Connection proxyConnection =(Connection) Proxy.newProxyInstance(clazz.getClassLoader(),
                        new Class<?>[]{Connection.class},
                        (proxy,method,args)-> {
                            if (method.getName().equals("close")) {
                                System.out.println("connection closing");
                                connectionPool.put(conn); // got it, it returns a connection to a pool when you close. 
                                // would propose to document the usage idea where it's not straightforward
                                System.out.println( "connection pool size="+connectionPool.size());
                            } else {
                                return method.invoke(conn, args);
                            }
                            return null; // would raise to if block
                        });
                connectionPool.put(proxyConnection);
            }
        } catch (Exception e) {
            e.printStackTrace(); // use logger.error()
            throw new RuntimeException(e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        for(Connection conn : realConnections){
            try {
                if(!conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
