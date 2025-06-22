package Dao;

import Entities.ExchangeRate;
import java.sql.*;
import java.util.List;

public class ExchangeRateDao extends CRUDImpl<ExchangeRate>{

    private static final String SELECT_BY_CURRENCIES_CODECOMBO = " SELECT ExchangeRates.* FROM ExchangeRates\n" +
            "JOIN Currencies C1 ON ExchangeRates.BaseCurrencyId = C1.ID\n" +
            "JOIN Currencies C2 ON ExchangeRates.TargetCurrencyId = C2.ID\n" +
            "WHERE C1.Code=? AND C2.Code=?";

    public ExchangeRateDao(Connection conn){
        super(ExchangeRate.class,conn);
    }

    @Override
    public List<ExchangeRate> selectAll() throws SQLException, InstantiationException, IllegalAccessException {
        return super.selectAll();
    }

    @Override
    public int insert(ExchangeRate obj) throws SQLException, IllegalAccessException {
        return super.insert(obj);
    }

    public ExchangeRate getByCodeCombo(String baseCode, String targetCode) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_BY_CURRENCIES_CODECOMBO)) {
            stmt.setString(1, baseCode);
            stmt.setString(2, targetCode);
            ResultSet rs = stmt.executeQuery();
            ExchangeRate er = new ExchangeRate.Builder().build();
            if(rs.next()) {
                er = new ExchangeRate.Builder()
                        .setId(rs.getInt("ID"))
                        .setRate(rs.getBigDecimal("Rate"))
                        .setBaseCurrencyId(rs.getInt("BaseCurrencyId"))
                        .setTargetCurrencyId(rs.getInt("TargetCurrencyId"))
                        .build();
            }
            return er;
        }
    }

    @Override
    public ExchangeRate getByID(int id) throws SQLException, InstantiationException, IllegalAccessException {
        return super.getByID(id);
    }

    @Override
    public int update(ExchangeRate obj) throws SQLException, IllegalAccessException {
        return super.update(obj);
    }

    @Override
    public int delete(ExchangeRate obj) throws SQLException, IllegalAccessException {
        return super.delete(obj);
    }
}
