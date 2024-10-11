import Dao.CurrencyDao;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ConnectionPoolTest {
    @Test
    public void testConPool(){
        String URL = "jdbc:sqlite:D:/JavaWorkSpace/CurrencyExchanger/target/classes/database.db";
        List<Connection> connections = new ArrayList<>();
        try {
            //старая версия
            Class.forName("org.sqlite.JDBC");
            BlockingQueue<Connection> connectionPool = new ArrayBlockingQueue<>(10);
            Class clazz = Connection.class;
            Long startTime = System.nanoTime();
            for(int i = 0 ; i<10; i++){
                Connection conn = DriverManager.getConnection(URL);
                connections.add(conn);
                Connection proxyConnection = (Connection) Proxy.newProxyInstance(clazz.getClassLoader(),
                        new Class<?>[]{Connection.class},
                        (proxy,method,args)-> {
                            if (method.getName().equals("close")) {
                                System.out.println("connection closing");
                                connectionPool.put(conn);
                            } else {
                                return method.invoke(conn, args);
                            }
                            return null;
                        });
                connectionPool.put(proxyConnection);
            }
            Long endTime = System.nanoTime();
            System.out.println(endTime-startTime);
            try(Connection connection = connectionPool.take()){
                System.out.println(connectionPool.size());
                CurrencyDao dao = new CurrencyDao(connection);
                System.out.println(dao.selectAll());
            }
            System.out.println(connectionPool.size());

            for (Connection conn : connections){
                conn.close();
            }
            for(Connection conn : connectionPool){
                System.out.println(conn.isClosed());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
