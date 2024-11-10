package DTO;

import Entities.Currency;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.SQLException;

public class ExchangeDTO {
    Currency baseCurrency; // privite modifier missing. Also can be a java record.
    Currency targetCurrency;
    BigDecimal rate;
    BigDecimal amount;
    BigDecimal convertedAmount;

    private ExchangeDTO(ExchangeDTO.Builder builder){
        this.convertedAmount = builder.convertedAmount;
        this.amount = builder.amount;
        this.rate = builder.rate;
        this.baseCurrency = builder.baseCurrency;
        this.targetCurrency = builder.targetCurrency;
    }

    public static class Builder{
        BigDecimal amount;
        BigDecimal convertedAmount;
        private BigDecimal rate;
        private Currency baseCurrency;
        private Currency targetCurrency;

        public ExchangeDTO.Builder setAmount(BigDecimal amount){
            this.amount = amount;
            return this;
        }
        public ExchangeDTO.Builder setConvertedAmount(BigDecimal convertedAmount){
            this.convertedAmount = convertedAmount;
            return this;
        }
        public ExchangeDTO.Builder setRate(BigDecimal rate){
            this.rate = rate;
            return this;
        }
        public ExchangeDTO.Builder setBaseCurrency(Currency baseCurrency){
            this.baseCurrency = baseCurrency;
            return this;
        }
        public ExchangeDTO.Builder setTargetCurrency(Currency targetCurrency){
            this.targetCurrency = targetCurrency;
            return this;
        }
        public ExchangeDTO build(){
            return new ExchangeDTO(this);
        }
    }

    @Override
    public String toString() {
        return "ExchangeDTO{" +
                "baseCurrency=" + baseCurrency +
                ", targetCurrency=" + targetCurrency +
                ", rate=" + rate +
                ", amount=" + amount +
                ", convertedAmount=" + convertedAmount +
                '}';
    }

}
