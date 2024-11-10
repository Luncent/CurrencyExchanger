package DTO;

import Entities.Currency;

import java.math.BigDecimal;

public class ExchangeRateDTO {
    public int id; // public is not a recommended choice, can be easily amended in a wrong place.
    public BigDecimal rate;
    public Currency baseCurrency;
    public Currency targetCurrency;

    @Override
    public String toString() {
        return "ExchangeRateDTO{" +
                "id=" + id +
                ", rate=" + rate +
                ", baseCurrency=" + baseCurrency +
                ", targetCurrency=" + targetCurrency +
                '}';
    }

    public boolean isValid(){
        return id!=0 && rate.compareTo(BigDecimal.ZERO)!=0 && baseCurrency!=null && targetCurrency != null;
    }

    private ExchangeRateDTO(ExchangeRateDTO.Builder builder){
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

        public ExchangeRateDTO.Builder setId(int id){
            this.id = id;
            return this;
        }
        public ExchangeRateDTO.Builder setRate(BigDecimal rate){
            this.rate = rate;
            return this;
        }
        public ExchangeRateDTO.Builder setBaseCurrency(Currency baseCurrency){
            this.baseCurrency = baseCurrency;
            return this;
        }
        public ExchangeRateDTO.Builder setTargetCurrency(Currency targetCurrency){
            this.targetCurrency = targetCurrency;
            return this;
        }
        public ExchangeRateDTO build(){
            return new ExchangeRateDTO(this);
        }
    }
}
