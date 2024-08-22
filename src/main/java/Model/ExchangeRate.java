package Model;

import DataBase.DB;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class ExchangeRate {
    private int id;
    private BigDecimal rate;
    private Currency baseCurrency;
    private Currency targetCurrency;

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
                return new ExchangeRate(-1,null,null,null);
            case 2:
                return new ExchangeRate(-2,null,null,null);
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
    public ExchangeRate(int id, BigDecimal rate, Currency baseCurrency, Currency targetCurrency) {
        this.id = id;
        this.rate = rate;
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
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
/*    public static void main(String[] args){
        try{
            addExRate("USD","BY", BigDecimal.valueOf(3.7023));
            //System.out.println(er);
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }*/
}
