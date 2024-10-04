package Entities;

import Annotations.Column;
import Annotations.Id;
import Annotations.Table;
import Dao.DB;

import java.sql.SQLException;
import java.util.List;

@Table(name="Currencies")
public class Currency {
    @Id
    @Column(name="ID")
    private int id;
    @Column(name="Code")
    private String code;
    @Column(name="FullName")
    private String name;
    @Column(name="Sign")
    private String sign;

    //TO DO убрать
    public Currency(int id, String sign, String name, String code) {
        this.id = id;
        this.sign = sign;
        this.name = name;
        this.code = code;
    }

    @Override
    public String toString() {
        return "Currency{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }

    public Currency() {
    }

    private Currency(Builder builder) {
        this.id = builder.id;
        this.code = builder.code;
        this.name = builder.name;
        this.sign = builder.sign;
    }

    public static class Builder{
        private int id;
        private String code;
        private String name;
        private String sign;

        public Builder setId(int id){
            this.id = id;
            return this;
        }
        public Builder setCode(String code){
            if(code.length()!=3){
                throw new IllegalArgumentException("code length must be 3");
            }
            this.code = code;
            return this;
        }
        public Builder setName(String name){
            this.name = name;
            return this;
        }
        public Builder setSign(String sign){
            this.sign = sign;
            return this;
        }

        /**
         *
         * @throws IllegalArgumentException if code||name||sign is null
         * @return Currency
         */
        public Currency build(){
            if(code==null || name == null || sign == null){
                throw new IllegalArgumentException("код, имя и знак валюты обязательны к заполению");
            }
            return new Currency(this);
        }
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
                return new Currency.Builder()
                        .setId(-1)
                        .setSign("-1")
                        .setCode("-1")
                        .setName("-1")
                        .build();
            case 2:
                return new Currency.Builder()
                        .setId(-2)
                        .setSign("-2")
                        .setCode("-22")
                        .setName("-2")
                        .build();
            default:
                return null;
        }
    }
}
