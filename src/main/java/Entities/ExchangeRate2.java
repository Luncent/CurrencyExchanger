package Entities;

import Annotations.Column;
import Annotations.Id;
import Annotations.Table;

import java.math.BigDecimal;

@Table(name = "ExchangeRates")
public class ExchangeRate2 {
    @Id
    @Column(name = "ID")
    private int id;
    @Column(name = "BaseCurrencyId")
    private int baseCurrencyId;
    @Column(name = "TargetCurrencyId")
    private int targetCurrencyId;
    @Column(name = "Rate")
    private BigDecimal rate;

    public ExchangeRate2() {
    }

    private ExchangeRate2(Builder builder){
        this.id = builder.id;
        this.rate = builder.rate;
        this.baseCurrencyId = builder.baseCurrencyId;
        this.targetCurrencyId = builder.targetCurrencyId;
    }

    public static class Builder{
        private int id;
        private BigDecimal rate;
        private int baseCurrencyId;
        private int targetCurrencyId;

        public Builder setId(int id) {
            this.id = id;
            return this;
        }
        public Builder setRate(BigDecimal rate) {
            this.rate = rate;
            return this;
        }
        public Builder setBaseCurrencyId(int baseCurrencyId) {
            this.baseCurrencyId = baseCurrencyId;
            return this;
        }
        public Builder setTargetCurrencyId(int targetCurrencyId) {
            this.targetCurrencyId = targetCurrencyId;
            return this;
        }
        public ExchangeRate2 build(){
            return new ExchangeRate2(this);
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTargetCurrencyId() {
        return targetCurrencyId;
    }

    public void setTargetCurrencyId(int targetCurrencyId) {
        this.targetCurrencyId = targetCurrencyId;
    }

    public int getBaseCurrencyId() {
        return baseCurrencyId;
    }

    public void setBaseCurrencyId(int baseCurrencyId) {
        this.baseCurrencyId = baseCurrencyId;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    @Override
    public String toString() {
        return "ExchangeRate2{" +
                "id=" + id +
                ", baseCurrencyId=" + baseCurrencyId +
                ", targetCurrencyId=" + targetCurrencyId +
                ", rate=" + rate +
                '}';
    }
}
