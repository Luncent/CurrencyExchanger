import Annotations.Table;
import Dao.CRUDImpl;
import Dao.CurrencyDao;
import Entities.Currency;
import Entities.ExchangeRate;
import Entities.ExchangeRate2;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class TestReflectionForDao {
    private static final String URL = "jdbc:sqlite:D:/JavaWorkSpace/CurrencyExchanger/target/classes/database.db";
    @Test
    public void testReflectionSelect() throws NoSuchFieldException, SQLException, InstantiationException, IllegalAccessException {
        try(Connection connection = DriverManager.getConnection(URL)){
            CRUDImpl<Currency> crud = new CRUDImpl<>(Currency.class, connection);
            List<Currency> currencyList =  crud.selectAll();
            System.out.println(currencyList);

            CRUDImpl<ExchangeRate2> crud2 = new CRUDImpl<>(ExchangeRate2.class, connection);
            List<ExchangeRate2> ExchangeRate2List =  crud2.selectAll();
            System.out.println(ExchangeRate2List);
        }
    }

    @Test
    public void testReflectionInsert() throws NoSuchFieldException, SQLException, InstantiationException, IllegalAccessException {
        try(Connection connection = DriverManager.getConnection(URL)){
            CRUDImpl<Currency> crud = new CRUDImpl<>(Currency.class, connection);
            CRUDImpl<ExchangeRate2> crud2 = new CRUDImpl<>(ExchangeRate2.class, connection);
            Currency currency = new Currency.Builder().setName("NyCurr")
                    .setSign("wed").setCode("Cod").setId(1).build();
            ExchangeRate2 er = new ExchangeRate2.Builder().setRate(BigDecimal.valueOf(0.224))
                            .setId(1).setBaseCurrencyId(1).setTargetCurrencyId(2).build();
            crud.insert(currency);
            crud2.insert(er);
        }
    }

    @Test
    public void testReflectionGetById() throws NoSuchFieldException, SQLException, InstantiationException, IllegalAccessException {
        try(Connection connection = DriverManager.getConnection(URL)){
            CRUDImpl<Currency> crud = new CRUDImpl<>(Currency.class, connection);
            CRUDImpl<ExchangeRate2> crud2 = new CRUDImpl<>(ExchangeRate2.class, connection);

            System.out.println(crud.getByID(1));
            System.out.println(crud2.getByID(1));
        }
    }

    @Test
    public void testReflectionUPDATE() throws NoSuchFieldException, SQLException, InstantiationException, IllegalAccessException {
        try(Connection connection = DriverManager.getConnection(URL)){
            CRUDImpl<Currency> crud = new CRUDImpl<>(Currency.class, connection);
            CRUDImpl<ExchangeRate2> crud2 = new CRUDImpl<>(ExchangeRate2.class, connection);
            Currency currency = new Currency.Builder().setName("NyNEW")
                    .setSign("New").setCode("NEW").setId(9).build();
            ExchangeRate2 er = new ExchangeRate2.Builder().setRate(BigDecimal.valueOf(0.802))
                    .setId(12).setBaseCurrencyId(1).setTargetCurrencyId(2).build();
            crud.update(currency);
            crud2.update(er);
        }
    }

    @Test
    public void testReflectionDELETE() throws NoSuchFieldException, SQLException, InstantiationException, IllegalAccessException {
        try(Connection connection = DriverManager.getConnection(URL)){
            CRUDImpl<Currency> crud = new CRUDImpl<>(Currency.class, connection);
            CRUDImpl<ExchangeRate2> crud2 = new CRUDImpl<>(ExchangeRate2.class, connection);
            Currency currency = new Currency.Builder().setName("NyNEW")
                    .setSign("New").setCode("NEW").setId(9).build();
            ExchangeRate2 er = new ExchangeRate2.Builder().setRate(BigDecimal.valueOf(0.802))
                    .setId(12).setBaseCurrencyId(1).setTargetCurrencyId(2).build();
            crud.delete(currency);
            crud2.delete(er);
        }
    }

}
