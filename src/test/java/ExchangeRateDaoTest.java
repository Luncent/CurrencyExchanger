import Dao.CurrencyDao;
import Dao.ExchangeRateDao;
import Entities.Currency;
import Entities.ExchangeRate;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class ExchangeRateDaoTest {
    private static final String URL = "jdbc:sqlite:D:/JavaWorkSpace/CurrencyExchanger/target/classes/database.db";
    @Test
    public void select() throws SQLException, InstantiationException, IllegalAccessException {
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
    public void update() throws SQLException, InstantiationException, IllegalAccessException {
        try(Connection conn = DriverManager.getConnection(URL)){
            ExchangeRateDao erdao = new ExchangeRateDao(conn);
            ExchangeRate er = erdao.getByID(3);
            er.setRate(BigDecimal.valueOf(2.24));
            erdao.update(er);
        }
    }
}
