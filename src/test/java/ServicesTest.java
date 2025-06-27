import DTO.ExchangeRateDTO;
import Entities.Currency;
import Entities.ExchangeRate;
import Exceptions.MyException;
import Exceptions.NotFoundException;
import Exceptions.RowExists;
import Services.CountExchangeService;
import Services.CurrencyService;
import Services.ExchangeRateService;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ServicesTest {
    List<Connection> realConnections = new ArrayList<>();
    BlockingQueue<Connection> connectionPool = new ArrayBlockingQueue<>(10);
    String URL = "jdbc:sqlite:D:/JavaWorkSpace/CurrencyExchanger/target/classes/database.db";
   /* private void initConnectionPool(){
        try {
            //старая версия
            Class.forName("org.sqlite.JDBC");
            Class clazz = Connection.class;
            for(int i = 0 ; i<10; i++){
                Connection conn = DriverManager.getConnection(URL);
                realConnections.add(conn);
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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void closeConnections() throws SQLException {
        for (Connection conn : realConnections){
            if(!conn.isClosed()) {
                conn.close();
                System.out.println("cleaning connection");
            }
        }
    }
    @Test
    public void testCurrencySelectAll() throws SQLException, InterruptedException, InstantiationException, IllegalAccessException {
       initConnectionPool();
       CurrencyService cs = new CurrencyService(connectionPool,URL);
       System.out.println(cs.getAll());
       closeConnections();
    }
    @Test
    public void testCurrencyGetByCOde() throws SQLException, InterruptedException, InstantiationException, IllegalAccessException, NotFoundException {
        initConnectionPool();
        CurrencyService cs = new CurrencyService(connectionPool,URL);
        System.out.println(cs.getByCode("BYN"));
        closeConnections();
    }
    @Test
    public void testCurrencyAdd() throws SQLException, InterruptedException, InstantiationException, IllegalAccessException {
        initConnectionPool();
        Currency currency = new Currency.Builder()
                .setCode("GHJ")
                .setSign("^")
                .setName("DOLLARS")
                .build();
        CurrencyService cs = new CurrencyService(connectionPool,URL);
        try {
            System.out.println(cs.add(currency));
        }
        catch (MyException | RowExists ex){
            System.out.println(ex.getMessage());
        }
        closeConnections();
    }

    @Test
    public void testExchangeRatesGetAll() throws SQLException, InterruptedException, InstantiationException, IllegalAccessException {
        initConnectionPool();
        ExchangeRateService erservice = new ExchangeRateService(connectionPool,URL);
        System.out.println(erservice.getAll());
        closeConnections();
    }

    @Test
    public void testExchangeRateByCodes() throws SQLException, InterruptedException, InstantiationException, IllegalAccessException, NotFoundException {
        initConnectionPool();
        ExchangeRateService erservice = new ExchangeRateService(connectionPool,URL);
        ExchangeRateDTO er = erservice.getByCurrenciesCodes("BYN","USD");
        if(er.isValid()){
            System.out.println(er);
        }
        else{
            System.out.println("empty");
        }
        closeConnections();
    }
    @Test
    public void testExchangeRateAdd() throws SQLException, InterruptedException, InstantiationException, IllegalAccessException {
        initConnectionPool();
        ExchangeRateService erservice = new ExchangeRateService(connectionPool,URL);
        ExchangeRate exchangeRate = new ExchangeRate.Builder()
                .setRate(BigDecimal.ONE)
                .setBaseCurrencyId(2)
                .setTargetCurrencyId(1)
                .build();
        try {
            ExchangeRateDTO er = erservice.add("USD","BYN",BigDecimal.ONE);
            if (er.isValid()) {
                System.out.println(er);
            } else {
                System.out.println("empty");
            }
        }
        catch (Exception ex){
            System.out.println(ex.getMessage());
        }
        closeConnections();
    }

    @Test
    public void testExchangeRateUpdate() throws SQLException, InterruptedException, InstantiationException, IllegalAccessException {
        initConnectionPool();
        ExchangeRateService erservice = new ExchangeRateService(connectionPool,URL);
        try {
            ExchangeRateDTO er = erservice.update("USD","AFN", BigDecimal.valueOf(3.2));
            if (er.isValid()) {
                System.out.println(er);
            } else {
                System.out.println("empty");
            }
        }
        catch (Exception ex){
            System.out.println(ex.getMessage());
        }
        closeConnections();
    }

    @Test
    public void testgetExchange() throws SQLException, InterruptedException, InstantiationException, IllegalAccessException {
        initConnectionPool();
        CountExchangeService service = new CountExchangeService(connectionPool,URL);
        try {
            System.out.println(service.getExchange("RUB","BYN", 20));
        }
        catch (Exception ex){
            System.out.println(ex.getMessage());
        }
        closeConnections();
    }*/
}
