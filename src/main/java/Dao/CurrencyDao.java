package Dao;

import Entities.Currency;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CurrencyDao extends CRUDImpl<Currency>{
    private static final String SELECT_BY_CODE = "SELECT * FROM Currencies WHERE Code = ?";

    public CurrencyDao(Connection connection){
        super(Currency.class, connection);
    }

    // you could just omit this methods, impl. in super class, just in case.
    @Override
    public List<Currency> selectAll() throws SQLException, InstantiationException, IllegalAccessException {
        return super.selectAll();
    }

    @Override
    public int insert(Currency currency) throws SQLException, IllegalAccessException {
        return super.insert(currency);
    }

    public Currency getByCode(String targetCode) throws SQLException, NullPointerException{
        Currency currency = Currency.createMockObj();
        try(PreparedStatement stmt = conn.prepareStatement(SELECT_BY_CODE)){
            stmt.setString(1, targetCode);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
                currency = new Currency.Builder()
                        .setId(rs.getInt("ID"))
                        .setCode(rs.getString("Code"))
                        .setName(rs.getString("FullName"))
                        .setSign(rs.getString("Sign"))
                        .build();
            }
        }
        return currency;
    }

    @Override
    public int update(Currency obj) throws SQLException, IllegalAccessException {
        return super.update(obj);
    }

    @Override
    public Currency getByID(int id) throws SQLException, InstantiationException, IllegalAccessException {
        return super.getByID(id);
    }

    @Override
    public int delete(Currency obj) throws SQLException, IllegalAccessException {
        return super.delete(obj);
    }
}
