package Entities;

import Annotations.Column;
import Annotations.Id;
import Annotations.Table;

import java.sql.SQLException;
import java.util.List;

@Table(name="Currencies") // table names are usually in small letters
public class Currency { // you can use java records from java 17
    @Id
    @Column(name="ID") // per naming same, small letters, _ as delimiter usually
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

        public Currency build(){
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

    public static Currency createMockObj() {
        return new Currency.Builder()
                .setId(0)
                .setSign("-1")
                .setCode("-11")
                .setName("-1")
                .build();
    }
    public static boolean checkMock(Currency currency){
        return currency.getCode().equals("-11");
    }
}
