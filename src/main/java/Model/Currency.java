package Model;

import DataBase.DB;

import java.sql.SQLException;
import java.util.List;

public class Currency {
    private int id;
    private String code;
    private String name;
    private String sign;

    @Override
    public String toString() {
        return "Currency{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }

    public Currency(int id, String code, String name, String sign) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.sign = sign;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public static List<Currency> getAllCurrencies() throws SQLException {
        return DB.getCurrencies();
    }
    public static Currency getCertCurrency(String CurrencyCode) throws SQLException{
        return DB.getCurrency(CurrencyCode);
    }
    public static int addCurrency(Currency newCurrency) throws SQLException{
        return DB.addCurrency(newCurrency);
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
    public static Currency createMockObj(int type){
        switch(type){
            case 1://
                return new Currency(-1,null,null,null);
            case 2:
                return new Currency(-2,null,null,null);
            default:
                return null;
        }
    }
}
