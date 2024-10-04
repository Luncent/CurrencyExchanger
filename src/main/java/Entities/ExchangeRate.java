package Entities;

import Dao.DB;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class ExchangeRate {
    private int id;
    private BigDecimal rate;
    private Currency baseCurrency;
    private Currency targetCurrency;

    public ExchangeRate(int id, BigDecimal rate,Currency baseCurrency, Currency targetCurrency) {
        this.id = id;
        this.targetCurrency = targetCurrency;
        this.baseCurrency = baseCurrency;
        this.rate = rate;
    }

    private ExchangeRate(Builder builder){
        this.id = builder.id;
        this.rate = builder.rate;
        this.baseCurrency = builder.baseCurrency;
        this.targetCurrency = builder.targetCurrency;
    }

    public static class Builder{
        private int id;
        private BigDecimal rate;
        private Currency baseCurrency;
        private Currency targetCurrency;

        public Builder setId(int id){
            this.id = id;
            return this;
        }
        public Builder setRate(BigDecimal rate){
            this.rate = rate;
            return this;
        }
        public Builder setBaseCurrency(Currency baseCurrency){
            this.baseCurrency = baseCurrency;
            return this;
        }
        public Builder setTargetCurrency(Currency targetCurrency){
            this.targetCurrency = targetCurrency;
            return this;
        }
        public ExchangeRate build(){
            if(baseCurrency == null || targetCurrency == null || rate ==null){
                throw new IllegalArgumentException("both currencies and rate must be initialized");
            }
            return new ExchangeRate(this);
        }
    }

    public int chkMock(){
        if(id == -1){
            return 1;
        }
        if(id == -2){
            return 2;
        }
        else{
            return 0;
        }
    }
    public static ExchangeRate createMockObj(int type){
        switch(type){
            case 1://
                return new ExchangeRate.Builder()
                        .setId(-1)
                        .setRate(BigDecimal.valueOf(-1))
                        .setBaseCurrency(Currency.createMockObj(1))
                        .setTargetCurrency(Currency.createMockObj(1))
                        .build();
            case 2:
                return new ExchangeRate.Builder()
                        .setId(-2)
                        .setRate(BigDecimal.valueOf(-2))
                        .setBaseCurrency(Currency.createMockObj(2))
                        .setTargetCurrency(Currency.createMockObj(2))
                        .build();
            default:
                return null;
        }
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    @Override
    public String toString() {
        return "ExchangeRate{" +
                "id=" + id +
                ", rate=" + rate +
                ", baseCurrency=" + baseCurrency +
                ", targetCurrency=" + targetCurrency +
                '}';
    }


    public ExchangeRate(){

    }
    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public Currency getTargetCurrency() {
        return targetCurrency;
    }

    public void setTargetCurrency(Currency targetCurrency) {
        this.targetCurrency = targetCurrency;
    }

    public Currency getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(Currency baseCurrency) {
        this.baseCurrency = baseCurrency;
    }
    public static List<ExchangeRate> getAllExRates() throws SQLException {
        return DB.getExchangeRates();
    }
    public static ExchangeRate getCertExRate(String baseCurCode, String targetCurCode) throws SQLException{
        return DB.getExchangeRate(baseCurCode,targetCurCode);
    }
    public static ExchangeRate getCertExRate(String codeCombo) throws SQLException{
        return DB.getExchangeRate(codeCombo);
    }
    public static void addExRate(String baseCurCode, String targetCurCode, BigDecimal rate) throws SQLException{
        DB.addExchangeRate(baseCurCode, targetCurCode, rate);
    }
    public static ExchangeRate updateExRate(String codeCombo, BigDecimal newRate) throws SQLException{
        return DB.updateExchangeRate(codeCombo,newRate);
    }
}
