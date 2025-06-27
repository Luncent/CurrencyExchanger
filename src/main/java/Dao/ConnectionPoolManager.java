package Dao;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ConnectionPoolManager {

    private final int POOL_SIZE;
    private final String CONNECTION_STRING;
    //private final BlockingQueue<Connection> connectionPool;
    private final Deque<Connection> connectionPool;
    private final List<Connection> realConnections;

    public ConnectionPoolManager(int POOL_SIZE, String CONNECTION_STRING) {
        this.POOL_SIZE = POOL_SIZE;
        this.CONNECTION_STRING = CONNECTION_STRING;
        //connectionPool = new ArrayBlockingQueue<>(this.POOL_SIZE);
        connectionPool = new ArrayDeque<>(this.POOL_SIZE);
        realConnections = new ArrayList<>(this.POOL_SIZE);
        setupConnectionPool();
    }

    public Connection getConnection() throws InterruptedException {
        //Connection connection = connectionPool.take();
        Connection connection = connectionPool.poll();
        System.out.println("taking connection, available connections: " + connectionPool.size());
        return connection;
    }

    public void closeConnections(){
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

    private void setupConnectionPool() {
        try {
            //старая версия
            Class.forName("org.sqlite.JDBC");
            Class clazz = Connection.class;

            for(int i = 0 ; i<10; i++){
                Connection conn = DriverManager.getConnection(CONNECTION_STRING);
                realConnections.add(conn);

                Connection proxyConnection =(Connection) Proxy.newProxyInstance(clazz.getClassLoader(),
                        new Class<?>[]{Connection.class},
                        (proxy,method,args)-> {
                            if (method.getName().equals("close")) {
                                System.out.println("connection closing");
                                //connectionPool.put((Connection)proxy);
                                conn.setAutoCommit(true);
                                connectionPool.push((Connection) proxy);
                                System.out.println( "connection pool size="+connectionPool.size());
                            } else {
                                return method.invoke(conn, args);
                            }
                            return null;
                        });
                //connectionPool.put(proxyConnection);
                connectionPool.push(proxyConnection);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
