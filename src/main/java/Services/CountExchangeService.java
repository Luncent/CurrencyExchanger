package Services;

import DTO.ExchangeDTO;
import Dao.CurrencyDao;
import Dao.ExchangeRateDao;
import Entities.Currency;
import Exceptions.MyException;
import Exceptions.NotFoundException;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;

public class CountExchangeService {
    private final String URL;
    private final BlockingQueue<Connection> connectionPool;
    public CountExchangeService(BlockingQueue<Connection> connectionPool, String URL){
        this.connectionPool = connectionPool;
        this.URL = URL;
    }
    public ExchangeDTO getExchange(String baseCurCode, String targetCurCode, double amount)
            throws SQLException, InterruptedException, MyException, NotFoundException {
        Connection conn = connectionPool.take();
        try{
            conn.setAutoCommit(false);

            CurrencyDao currencyDao = new CurrencyDao(conn);
            Currency baseCurrency = currencyDao.getByCode(baseCurCode);
            Currency targetCurrency = currencyDao.getByCode(targetCurCode);

            if(baseCurrency.getId()==0 || targetCurrency.getId()==0){
                throw new NotFoundException("currency or currencies dont exist");
            }

            ExchangeRateDao erDao = new ExchangeRateDao(conn);
            BigDecimal convertedAmount = BigDecimal.ZERO;
            BigDecimal rate = BigDecimal.ZERO;

            int scale = 30;
            RoundingMode rm = RoundingMode.HALF_UP;
            MathContext mathContext = new MathContext(scale,rm);

            if(baseCurCode.equals(targetCurCode)){
                convertedAmount = BigDecimal.valueOf(amount);
            }
            else if((rate = erDao.getByCodeCombo(baseCurCode,targetCurCode).getRate())!=null){
                convertedAmount = rate.multiply(BigDecimal.valueOf(amount),mathContext);
            } else if ((rate = erDao.getByCodeCombo(targetCurCode,baseCurCode).getRate())!=null){
                rate = BigDecimal.ONE.divide(rate,mathContext);
                convertedAmount = rate.multiply(BigDecimal.valueOf(amount),mathContext);
            }
            else {
                BigDecimal usd_baseRate = erDao.getByCodeCombo("USD",baseCurCode).getRate();
                BigDecimal usd_targetRate = erDao.getByCodeCombo("USD",targetCurCode).getRate();
                if(usd_targetRate==null || usd_baseRate==null){
                    throw new NotFoundException("exchange rate not found");
                }
                rate =  BigDecimal.ONE.divide(usd_baseRate,mathContext).multiply(usd_targetRate,mathContext);
                convertedAmount = rate.multiply(BigDecimal.valueOf(amount),mathContext);
            }

             ExchangeDTO dto = new ExchangeDTO.Builder()
                    .setBaseCurrency(baseCurrency)
                    .setTargetCurrency(targetCurrency)
                    .setRate(rate)
                    .setAmount(BigDecimal.valueOf(amount))
                    .setConvertedAmount(convertedAmount.setScale(2,RoundingMode.HALF_UP))
                    .build();

            conn.commit();
            return dto;
        }
        catch (Exception ex){
            try {
                conn.rollback();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            ex.printStackTrace();
            throw ex;
        }
        finally {
            try {
                conn.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
