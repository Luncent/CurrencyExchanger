import Annotations.Table;
import Dao.CurrencyDao;
import org.junit.jupiter.api.Test;
import Entities.Currency;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class CurrencyDaoTest {

    private static final String URL = "jdbc:sqlite:D:/JavaWorkSpace/CurrencyExchanger/src/database.db";
    @Test
    public void select() throws SQLException, InstantiationException, IllegalAccessException {
        try(Connection conn = DriverManager.getConnection(URL)){
            CurrencyDao dao = new CurrencyDao(conn);
            List<Currency> currencies = dao.selectAll();
            System.out.println(currencies);
        }
    }
    @Test
    public void getByCode() throws SQLException {
        Long startTime = System.nanoTime();
        try(Connection conn = DriverManager.getConnection(URL)){
            Long endTime = System.nanoTime();
            System.out.println(endTime-startTime);
            CurrencyDao dao = new CurrencyDao(conn);
            Currency currency = dao.getByCode("AUD");
            System.out.println(currency);
        }
    }
    @Test
    public void insert() throws SQLException, IllegalAccessException {
        try(Connection conn = DriverManager.getConnection(URL)){
            CurrencyDao dao = new CurrencyDao(conn);
            Currency currency = new Currency.Builder()
                    .setName("MyCurrency")
                    .setCode("MYC")
                    .setSign("%")
                    .build();
            dao.insert(currency);
            System.out.println(currency);
        }
    }

}
