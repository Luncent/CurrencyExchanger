package Dao;

import Entities.Currency;
import Entities.ExchangeRate;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExchangeRateDao implements CRUD<ExchangeRate>{

    private static final String SELECT_ALL = " SELECT ExchangeRates.*, C1.Code AS baseCode, C1.FullName AS baseName," +
            "C1.Sign AS baseSign, C2.Code AS targetCode, C2.FullName AS targetName,C2.Sign AS targetSign" +
            " FROM ExchangeRates\n" +
            "JOIN Currencies C1 on ExchangeRates.BaseCurrencyId = C1.ID\n" +
            "JOIN Currencies C2 on C2.ID = ExchangeRates.TargetCurrencyId";
    private static final String INSERT = " INSERT INTO ExchangeRates(BaseCurrencyId, TargetCurrencyId, Rate) VALUES (?,?,?)";
    private static final String SELECT_BY_CURRENCIES_CODECOMBO = " SELECT ExchangeRates.*, C1.Code AS BaseCode, C2.Code TargetCode, C1.Code AS C1_Code,\n" +
            "       C1.FullName AS C1_Name, C1.Sign AS C1_Sign, C2.Code AS C2_Code,\n" +
            "       C2.FullName AS C2_Name, C2.Sign AS C2_Sign FROM ExchangeRates\n" +
            "JOIN Currencies C1 ON ExchangeRates.BaseCurrencyId = C1.ID\n" +
            "JOIN Currencies C2 ON ExchangeRates.TargetCurrencyId = C2.ID\n" +
            "WHERE BaseCode=? AND TargetCode=?";
    private static final String UPDATE = " UPDATE ExchangeRates\n" +
            "SET Rate = ?,\n" +
            "BaseCurrencyId = ?,\n" +
            "TargetCurrencyId = ?\n" +
            "WHERE ID=?";

    private Connection conn;

    public ExchangeRateDao(Connection conn){
        this.conn=conn;
    }

    @Override
    public List<ExchangeRate> selectAll() throws SQLException {
        List<ExchangeRate> exchangeRates = new ArrayList<>();
        try (Statement erStmt = conn.createStatement()) {
            ResultSet rs = erStmt.executeQuery(SELECT_ALL);
            while (rs.next()) {
                Currency baseCurrency = new Currency.Builder()
                        .setId(rs.getInt("BaseCurrencyId"))
                        .setCode(rs.getString("baseCode"))
                        .setName(rs.getString("baseName"))
                        .setSign(rs.getString("baseSign"))
                        .build();
                Currency targetCurrency = new Currency.Builder()
                        .setId(rs.getInt("TargetCurrencyId"))
                        .setCode(rs.getString("targetCode"))
                        .setName(rs.getString("targetName"))
                        .setSign(rs.getString("targetSign"))
                        .build();
                ExchangeRate exRate = new ExchangeRate.Builder()
                        .setId(rs.getInt("ID"))
                        .setRate(rs.getBigDecimal("Rate"))
                        .setBaseCurrency(baseCurrency)
                        .setTargetCurrency(targetCurrency)
                        .build();
                exchangeRates.add(exRate);
            }
            return exchangeRates;
        }
    }

    @Override
    public int insert(ExchangeRate exchangeRate) throws SQLException{
        validateInsertUpdate(exchangeRate);
        try(PreparedStatement ins_stmt = conn.prepareStatement(INSERT)) {
            ins_stmt.setInt(1, exchangeRate.getBaseCurrency().getId());
            ins_stmt.setInt(2, exchangeRate.getTargetCurrency().getId());
            ins_stmt.setBigDecimal(3, exchangeRate.getRate());
            int result = ins_stmt.executeUpdate();
            return result;
        }
    }

    public boolean validateInsertUpdate(ExchangeRate er){
        if(er == null){
            throw new IllegalArgumentException("null received");
        }
        if(er.getBaseCurrency().getCode().equals(er.getTargetCurrency().getCode())){
            throw new IllegalArgumentException("can't create exchangeRate between one currency");
        }
        if(er.getRate().compareTo(BigDecimal.ZERO) < 0){
            throw new IllegalArgumentException("rate cant be <0");
        }
        return true;
    }

    public ExchangeRate getByCodeCombo(String baseCode, String targetCode) throws SQLException{
            try(PreparedStatement stmt = conn.prepareStatement(SELECT_BY_CURRENCIES_CODECOMBO)){
            stmt.setString(1,baseCode);
            stmt.setString(2,targetCode);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                Currency baseCur = new Currency.Builder()
                        .setId(rs.getInt("BaseCurrencyId"))
                        .setCode(rs.getString("C1_Code"))
                        .setName(rs.getString("C1_Name"))
                        .setSign(rs.getString("C1_Sign"))
                        .build();
                Currency targetCur = new Currency.Builder()
                        .setId(rs.getInt("TargetCurrencyId"))
                        .setCode(rs.getString("C2_Code"))
                        .setName(rs.getString("C2_Name"))
                        .setSign(rs.getString("C2_Sign"))
                        .build();
                ExchangeRate er = new ExchangeRate.Builder()
                        .setId(rs.getInt("ID"))
                        .setRate(rs.getBigDecimal("Rate"))
                        .setBaseCurrency(baseCur)
                        .setTargetCurrency(targetCur)
                        .build();
                return er;
            }
            else{
                return ExchangeRate.createMockObj(2);
            }
        }
    }

    @Override
    public ExchangeRate getByID(int id) throws SQLException {
        return null;
    }

    @Override
    public int update(ExchangeRate obj) throws SQLException {
        validateInsertUpdate(obj);
        try (PreparedStatement stmt = conn.prepareStatement(UPDATE)) {
            stmt.setBigDecimal(1, obj.getRate());
            stmt.setInt(2, obj.getBaseCurrency().getId());
            stmt.setInt(3, obj.getTargetCurrency().getId());
            stmt.setInt(4, obj.getId());
            return stmt.executeUpdate();
        }
    }

    @Override
    public int delete(ExchangeRate obj) throws SQLException {

        return 0;
    }
}
