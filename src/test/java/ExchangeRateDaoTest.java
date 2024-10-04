import Dao.CurrencyDao;
import Dao.ExchangeRateDao;
import Entities.Currency;
import Entities.ExchangeRate;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class ExchangeRateDaoTest {
    private static final String URL = "jdbc:sqlite:D:/JavaWorkSpace/CurrencyExchanger/target/classes/database.db";
    @Test
    public void select() throws SQLException {
        try(Connection conn = DriverManager.getConnection(URL)){
            ExchangeRateDao exrateDao = new ExchangeRateDao(conn);
            List<ExchangeRate> exchangeRates = exrateDao.selectAll();
            System.out.println(exchangeRates);
        }
    }
    @Test
    public void getByCodeCombo() throws SQLException {
        try(Connection conn = DriverManager.getConnection(URL)){
            ExchangeRateDao dao = new ExchangeRateDao(conn);
            ExchangeRate er = dao.getByCodeCombo("USD","BYN");
            System.out.println(er);
        }
    }
    @Test
    public void insert() throws SQLException {
        try(Connection conn = DriverManager.getConnection(URL)){
            CurrencyDao dao = new CurrencyDao(conn);
            Currency c1 = new Currency.Builder()
                    .setName("Name")
                    .setSign("cogde")
                    .setCode("sds")
                    .build();
            System.out.println(c1);
            ExchangeRateDao erdao = new ExchangeRateDao(conn);
            //int ret = erdao.insert(new ExchangeRate(BigDecimal.valueOf(0.94), c1,c2));
            //int ret = erdao.insert(new ExchangeRate(BigDecimal.valueOf(0.94),null,currency2));
            //System.out.println(ret);
        }
    }
}
