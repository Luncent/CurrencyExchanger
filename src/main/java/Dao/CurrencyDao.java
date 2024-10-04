package Dao;

import Entities.Currency;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CurrencyDao extends CRUDImpl<Currency>{
    private static final String SELECT_ALL = "SELECT * FROM Currencies";
    private static final String INSERT = "INSERT INTO Currencies(Code,FullName,Sign) VALUES (?,?,?)";
    private static final String SELECT_BY_CODE = "SELECT * FROM Currencies WHERE Code = ?";

    private Connection conn;
    public CurrencyDao(Connection connection){
        super(Currency.class, connection);
    }

    @Override
    public List<Currency> selectAll() throws SQLException {
        List<Currency> currencies = new ArrayList<>();
        try(PreparedStatement stmt = conn.prepareStatement(SELECT_ALL)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Currency currency = new Currency.Builder()
                        .setId(rs.getInt("ID"))
                        .setCode(rs.getString("Code"))
                        .setName(rs.getString("FullName"))
                        .setSign(rs.getString("Sign"))
                        .build();
                currencies.add(currency);
            }
            return currencies;
        }
    }

    @Override
    public int insert(Currency currency) throws SQLException {
        validateInsert(currency);
        try(PreparedStatement stmt = conn.prepareStatement(INSERT)) {
            stmt.setString(1, currency.getCode());
            stmt.setString(2, currency.getName());
            stmt.setString(3, currency.getSign());
            return stmt.executeUpdate();
        }
    }

    private boolean validateInsert(Currency currency){
        if(currency == null){
            throw new IllegalArgumentException("null передан");
        }
        return true;
    }

    public Currency getByCode(String targetCode) throws SQLException{
        Currency currency = Currency.createMockObj(2);
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
    public Currency getByID(int id) {
        return null;
    }

    @Override
    public int delete(Currency obj) throws SQLException {
        return 0;
    }

    @Override
    public int update(Currency obj) {
        return 0;
    }
}
