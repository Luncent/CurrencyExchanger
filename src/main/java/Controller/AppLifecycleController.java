package Controller;

import Dao.ConnectionPoolManager;
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
    private static final int POOL_SIZE = 10;
    private ConnectionPoolManager connectionPoolManager;

    /*private List<Connection> realConnections = new ArrayList<>();
    private BlockingQueue<Connection> connectionPool = new ArrayBlockingQueue<>(10);*/
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        System.out.println("________________________________________________________________");
        connectionPoolManager = new ConnectionPoolManager(POOL_SIZE, URL);
        //createConnectionPool();
        CountExchangeService countExchangeService = new CountExchangeService(connectionPoolManager,URL);
        CurrencyService currencyService = new CurrencyService(connectionPoolManager,URL);
        ExchangeRateService exchangeRateService = new ExchangeRateService(connectionPoolManager,URL);

        ServletContext context = servletContextEvent.getServletContext();
        context.setAttribute("countExchangeService",countExchangeService);
        context.setAttribute("currencyService",currencyService);
        context.setAttribute("exchangeRateService",exchangeRateService);
    }

    /*private void createConnectionPool(){
        try {
            //старая версия
            Class.forName("org.sqlite.JDBC");
            Class clazz = Connection.class;

            for(int i = 0 ; i<10; i++){
                Connection conn = DriverManager.getConnection(URL);
                realConnections.add(conn);

                Connection proxyConnection =(Connection) Proxy.newProxyInstance(clazz.getClassLoader(),
                        new Class<?>[]{Connection.class},
                        (proxy,method,args)-> {
                            if (method.getName().equals("close")) {
                                System.out.println("connection closing");
                                connectionPool.put((Connection)proxy);
                                System.out.println( "connection pool size="+connectionPool.size());
                            } else {
                                return method.invoke(conn, args);
                            }
                            return null;
                        });
                connectionPool.put(proxyConnection);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }*/

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        /*for(Connection conn : realConnections){
            try {
                if(!conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }*/
        connectionPoolManager.closeConnections();
    }
}
