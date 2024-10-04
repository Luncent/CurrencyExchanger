package DTO;

import Dao.DB;
import Entities.Currency;
import Entities.ExchangeRate;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.SQLException;

public class Exchange {
    Currency baseCurrency;
    Currency targetCurrency;
    BigDecimal rate;
    BigDecimal amount;
    BigDecimal convertedAmount;

    public Currency getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(Currency baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public Currency getTargetCurrency() {
        return targetCurrency;
    }

    public void setTargetCurrency(Currency targetCurrency) {
        this.targetCurrency = targetCurrency;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getConvertedAmount() {
        return convertedAmount;
    }

    public void setConvertedAmount(BigDecimal convertedAmount) {
        this.convertedAmount = convertedAmount;
    }

    public Exchange(String baseCurrency, String targetCurrency, BigDecimal amount) throws SQLException{
        this.baseCurrency = DB.getCurrency(baseCurrency);
        this.targetCurrency = DB.getCurrency(targetCurrency);
        this.amount = amount;
    }

    public void calculateExchange() throws SQLException {
        String baseCur = baseCurrency.getCode();
        String targetCur = targetCurrency.getCode();
        if(baseCur.equals(targetCur)){
            rate=BigDecimal.ONE;
            convertedAmount = amount;
            return;
        }
        ExchangeRate er = ExchangeRate.getCertExRate(baseCur,targetCur);
        int scale = 30;
        RoundingMode rm = RoundingMode.HALF_UP;
        MathContext mathContext = new MathContext(scale,rm);
        if(er.chkMock()==0){
            //сценарий: курс найден
            rate = er.getRate();
            convertedAmount = amount.multiply(rate,mathContext);
            return;
        }
        er = ExchangeRate.getCertExRate(targetCur, baseCur);
        if(er.chkMock()==0){
            //сценарий: есть обратный курс
            rate = BigDecimal.ONE.divide(er.getRate(), mathContext);
            convertedAmount = amount.multiply(rate, mathContext);
            return;
        }
        er = ExchangeRate.getCertExRate("USD",baseCur);
        ExchangeRate er2 = ExchangeRate.getCertExRate("USD",targetCur);
        if(er.chkMock()==0 && er2.chkMock()==0){
            rate = BigDecimal.ONE.divide(er.getRate(), mathContext);
            rate = rate.multiply(er2.getRate(), mathContext);
            convertedAmount = amount.multiply(rate, mathContext);
        }
        else{
            return;
        }
    }
    public boolean checkCurrencies(){
        if(baseCurrency.chkMock()!=0 || targetCurrency.chkMock()!=0){
            return false;
        }
        else{
            return true;
        }
    }
    public boolean checkExchange(){
        if(rate == null || convertedAmount==null){
            return  false;
        }
        else return true;
    }
}
