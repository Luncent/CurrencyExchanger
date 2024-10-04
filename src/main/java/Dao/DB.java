package Dao;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import Entities.Currency;
import Entities.ExchangeRate;

public class DB {
    private static final String URL = "jdbc:sqlite:../webapps/CurrencyExchanger-1.0/WEB-INF/classes/database.db";
    //private static final String URL = "jdbc:sqlite:src/test/Queries/database.db";

    public static List<Currency> getCurrencies () throws SQLException{
        String query = "SELECT * FROM Currencies";
        List<Currency> currencies = new ArrayList<>();
        try(Connection conn = DriverManager.getConnection(URL); Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()) {
                Currency currency = new Currency(rs.getInt("ID"),rs.getString("Code"),
                        rs.getString("FullName"),rs.getString("Sign"));
                currencies.add(currency);
            }
            return currencies;
        }
    }
    //TO DO use join для атомарности
    public static List<ExchangeRate> getExchangeRates () throws SQLException {
        String query = "SELECT ExchangeRates.*, C1.Code AS baseCode, C1.FullName AS baseName," +
                "C1.Sign AS baseSign, C2.Code AS targetCode, C2.FullName AS targetName,C2.Sign AS targetSign" +
                " FROM ExchangeRates\n" +
                "JOIN Currencies C1 on ExchangeRates.BaseCurrencyId = C1.ID\n" +
                "JOIN Currencies C2 on C2.ID = ExchangeRates.TargetCurrencyId";
        List<ExchangeRate> exchangeRates = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(URL); Statement erStmt = conn.createStatement()) {
            ResultSet rs = erStmt.executeQuery(query);
            while (rs.next()) {
                Currency baseCurrency = new Currency(rs.getInt("BaseCurrencyId"), rs.getString("baseCode"),
                        rs.getString("baseName"), rs.getString("baseSign"));
                Currency targetCurrency = new Currency(rs.getInt("TargetCurrencyId"), rs.getString("targetCode"),
                        rs.getString("targetName"), rs.getString("targetSign"));
                ExchangeRate exRate = new ExchangeRate(rs.getInt("ID"),rs.getBigDecimal("Rate"),
                        baseCurrency, targetCurrency);
                exchangeRates.add(exRate);
            }
            return exchangeRates;
        }
    }
    public static Currency getCurrency (String targetCode) throws SQLException{
        if(targetCode == null || targetCode.isEmpty()){
            return Currency.createMockObj(1);
        }
        String query = "SELECT * FROM Currencies WHERE Code = ?";
        try(Connection conn = DriverManager.getConnection(URL); PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setString(1, targetCode);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
                return new Currency(rs.getInt("ID"), rs.getString("Code"),
                        rs.getString("FullName"), rs.getString("Sign"));
            }
            else{
                //not found
                return Currency.createMockObj(2);
            }
        }
    }
    public static int addCurrency (Currency currency) throws SQLException{
        if(currency == null){
            return -1;
        }
        String query = "INSERT INTO Currencies(Code,FullName,Sign) VALUES (?,?,?)";
        try(Connection conn = DriverManager.getConnection(URL); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, currency.getCode());
            stmt.setString(2, currency.getName());
            stmt.setString(3, currency.getSign());
            stmt.executeUpdate();
            return 0;
        }
    }
    //TO DO use join атомарность
    public static ExchangeRate getExchangeRate (String baseCode, String targetCode) throws SQLException{
        if(targetCode==null || baseCode==null || targetCode.isEmpty() || baseCode.isEmpty()){
            return ExchangeRate.createMockObj(1);
        }
        String query = "SELECT ExchangeRates.*, C1.Code AS baseCode, C1.FullName AS baseName,C1.Sign AS baseSign, C2.Code AS targetCode," +
                " C2.FullName AS targetName,C2.Sign AS targetSign FROM ExchangeRates\n" +
                "JOIN Currencies C1 on ExchangeRates.BaseCurrencyId = C1.ID\n" +
                "JOIN Currencies C2 on C2.ID = ExchangeRates.TargetCurrencyId\n" +
                "WHERE C1.Code=? AND C2.Code =?";
        try(Connection conn = DriverManager.getConnection(URL); PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setString(1,baseCode);
            stmt.setString(2,targetCode);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                Currency baseCurrency= new Currency(rs.getInt("BaseCurrencyId"), rs.getString("baseCode"),
                        rs.getString("baseName"), rs.getString("baseSign"));
                Currency targetCurrency = new Currency(rs.getInt("TargetCurrencyId"), rs.getString("targetCode"),
                        rs.getString("targetName"), rs.getString("targetSign"));
                return new ExchangeRate(rs.getInt("ID"),rs.getBigDecimal("Rate"),
                        baseCurrency, targetCurrency);
            }
            else{
                return ExchangeRate.createMockObj(2);
            }
        }
    }
    //TO DO атомарность
    public static int addExchangeRate(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate) throws SQLException {
        String insertQuery = "INSERT INTO ExchangeRates(BaseCurrencyId, TargetCurrencyId, Rate) VALUES (?,?,?)";
        String selCurrencyQuery = "SELECT * FROM Currencies WHERE Code = ?";
        Connection conn=null;
        PreparedStatement baseCur_stmt =null, targetCur_stmt=null, ins_stmt=null;
        try {
            conn = DriverManager.getConnection(URL);
            //transection begining
            conn.setAutoCommit(false);
            System.out.println("commit false");
            baseCur_stmt = conn.prepareStatement(selCurrencyQuery);
            targetCur_stmt = conn.prepareStatement(selCurrencyQuery);
            baseCur_stmt.setString(1, baseCurrencyCode);
            targetCur_stmt.setString(1, targetCurrencyCode);

            ResultSet baseCurRS = baseCur_stmt.executeQuery();
            ResultSet targetCurRS = targetCur_stmt.executeQuery();
            if(!baseCurRS.next() && !targetCurRS.next()){
                System.out.println("rs.next false");
                return -1;
            }
            else{
                ins_stmt = conn.prepareStatement(insertQuery);
                ins_stmt.setInt(1, baseCurRS.getInt("ID"));
                ins_stmt.setInt(2, targetCurRS.getInt("ID"));
                ins_stmt.setBigDecimal(3,rate);
                System.out.println("id "+baseCurRS.getInt("ID")+" "+targetCurRS.getInt("ID"));
                int result =  ins_stmt.executeUpdate();
                //commit
                conn.commit();
                System.out.println("after commit");
                return result;
            }
        }
        catch (SQLException ex){
            try {
                conn.rollback();
            }
            catch (NullPointerException e){}

            throw ex;
        }
        finally {
            try {
                conn.close();
                baseCur_stmt.close();
                targetCur_stmt.close();
                ins_stmt.close();
            }
            catch (NullPointerException ex){}
        }
    }

    public static ExchangeRate getExchangeRate(String codeCombo) throws SQLException{
        if(codeCombo==null || codeCombo.isEmpty()){
            return ExchangeRate.createMockObj(1);
        }
        String query = "SELECT ExchangeRates.*, concat(C1.Code,C2.Code) AS CodeCombo, C1.Code AS C1_Code,\n" +
                "       C1.FullName AS C1_Name, C1.Sign AS C1_Sign, C2.Code AS C2_Code,\n" +
                "       C2.FullName AS C2_Name, C2.Sign AS C2_Sign FROM ExchangeRates\n" +
                "JOIN Currencies C1 ON ExchangeRates.BaseCurrencyId = C1.ID\n" +
                "JOIN Currencies C2 ON ExchangeRates.TargetCurrencyId = C2.ID\n" +
                "WHERE CodeCombo=?";
        try(Connection conn = DriverManager.getConnection(URL); PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setString(1,codeCombo);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                Currency baseCur = new Currency(rs.getInt("BaseCurrencyId"),rs.getString("C1_Code"),
                        rs.getString("C1_Name"),rs.getString("C1_Sign"));
                Currency targetCur = new Currency(rs.getInt("TargetCurrencyId"),rs.getString("C2_Code"),
                        rs.getString("C2_Name"),rs.getString("C2_Sign"));
                ExchangeRate er = new ExchangeRate(rs.getInt("ID"),
                        rs.getBigDecimal("Rate"),baseCur,targetCur);
                return er;
            }
            else{
                return ExchangeRate.createMockObj(2);
            }
        }
    }
    public static ExchangeRate updateExchangeRate(String codeCombo, BigDecimal rate) throws SQLException{
        if(codeCombo == null || rate== null){
            return ExchangeRate.createMockObj(1);
        }
        String query = "UPDATE ExchangeRates\n" +
                "SET Rate = ?\n" +
                "WHERE ID=(SELECT ExchangeRates.ID FROM ExchangeRates\n" +
                "        JOIN Currencies C1 ON ExchangeRates.BaseCurrencyId = C1.ID\n" +
                "        JOIN Currencies C2 ON ExchangeRates.TargetCurrencyId = C2.ID\n" +
                "       WHERE CONCAT(C1.Code,C2.Code)=?)";
        try(Connection conn = DriverManager.getConnection(URL); PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setBigDecimal(1, rate);
            stmt.setString(2, codeCombo);
            if(stmt.executeUpdate()==0){
                return ExchangeRate.createMockObj(2);
            }
            return getExchangeRate(codeCombo);
        }
    }

    public static void main(String[] args) throws SQLException{
        try{
            System.out.println("try");
            throw new SQLException();
        }
        catch (SQLException ex){
            System.out.println("catch");
            throw new NullPointerException();
        }
        finally {
            System.out.println("finnaly");
            throw new NullPointerException();
        }
    }
}
