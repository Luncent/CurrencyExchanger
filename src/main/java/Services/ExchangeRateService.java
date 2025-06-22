package Services;

import DTO.ExchangeRateDTO;
import Dao.CurrencyDao;
import Dao.ExchangeRateDao;
import Entities.Currency;
import Entities.ExchangeRate;
import Exceptions.MyException;
import Exceptions.NotFoundException;
import Exceptions.RowExists;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class ExchangeRateService {
    private final String URL;
    private final BlockingQueue<Connection> connectionPool;
    public ExchangeRateService(BlockingQueue<Connection> connectionPool, String URL){
        this.connectionPool = connectionPool;
        this.URL = URL;
    }

    public List<ExchangeRateDTO> getAll() throws InterruptedException, SQLException, InstantiationException, IllegalAccessException {
        List<ExchangeRateDTO> exchangeRateDTOS = new ArrayList<>();
        Connection conn = connectionPool.take();
        try{
            ExchangeRateDao exchangeRateDao = new ExchangeRateDao(conn);
            CurrencyDao currencyDao = new CurrencyDao(conn);
            List<ExchangeRate> erEntities = exchangeRateDao.selectAll();
            for(ExchangeRate entity : erEntities){
                Currency base = currencyDao.getByID(entity.getBaseCurrencyId());
                Currency target = currencyDao.getByID(entity.getTargetCurrencyId());
                ExchangeRateDTO dto = new ExchangeRateDTO.Builder()
                        .setId(entity.getId())
                        .setRate(entity.getRate())
                        .setBaseCurrency(base)
                        .setTargetCurrency(target)
                        .build();
                exchangeRateDTOS.add(dto);
            }
            return exchangeRateDTOS;
        }
        catch (Exception ex){
            try {
                conn.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            ex.printStackTrace();
            throw ex;
        }
        finally {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public ExchangeRateDTO getByCurrenciesCodes(String baseCode, String targetCode) throws InterruptedException, SQLException,
            InstantiationException, IllegalAccessException, NotFoundException {
        Connection conn = connectionPool.take();
        try {
            conn.setAutoCommit(false);
            ExchangeRateDao exchangeRateDao = new ExchangeRateDao(conn);
            CurrencyDao currencyDao = new CurrencyDao(conn);
            ExchangeRate erEntity = exchangeRateDao.getByCodeCombo(baseCode, targetCode);
            if(erEntity.getId()==0){
                throw new NotFoundException("exchangeRate not found");
            }
            Currency base = currencyDao.getByID(erEntity.getBaseCurrencyId());
            Currency target = currencyDao.getByID(erEntity.getTargetCurrencyId());
            ExchangeRateDTO exchangeRateDTO = new ExchangeRateDTO.Builder()
                    .setId(erEntity.getId())
                    .setRate(erEntity.getRate())
                    .setBaseCurrency(base)
                    .setTargetCurrency(target)
                    .build();
            conn.commit();
            return exchangeRateDTO;
        }
        catch (Exception ex){
            try {
                conn.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            ex.printStackTrace();
            throw ex;
        }
        finally {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }



    public ExchangeRateDTO add(String baseCode, String targetCode, BigDecimal rate) throws InterruptedException,
            MyException, SQLException, RowExists, NotFoundException, IllegalAccessException {
        Connection conn = connectionPool.take();
        try{
            conn.setAutoCommit(false);
            CurrencyDao currencyDao = new CurrencyDao(conn);
            ExchangeRateDao erDao = new ExchangeRateDao(conn);
            Currency base = currencyDao.getByCode(baseCode);
            Currency target = currencyDao.getByCode(targetCode);
            if(base.getId()==0 || target.getId()==0){
                throw new NotFoundException("currency doesnt exist");
            }
            if(rate.compareTo(BigDecimal.ZERO)==-1){
                throw new MyException("rate cant be less than 0");
            }
            if(erDao.getByCodeCombo(base.getCode(),target.getCode()).getId()!=0){
                throw new RowExists("exchange rate between currencies exists");
            }
            if(base.getCode().equals(target.getCode())){
                throw new MyException("exchange rate between one currency not allowed");
            }
            ExchangeRate newExchangeRate = new ExchangeRate.Builder()
                    .setRate(rate)
                    .setBaseCurrencyId(base.getId())
                    .setTargetCurrencyId(target.getId())
                    .build();
            erDao.insert(newExchangeRate);
            ExchangeRate erEntity = erDao.getByCodeCombo(base.getCode(), target.getCode());
            ExchangeRateDTO exchangeRateDTO = new ExchangeRateDTO.Builder()
                    .setId(erEntity.getId())
                    .setRate(erEntity.getRate())
                    .setBaseCurrency(base)
                    .setTargetCurrency(target).build();
            conn.commit();
            return exchangeRateDTO;
        }
        catch (Exception ex){
            try {
                conn.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            ex.printStackTrace();
            throw ex;
        }
        finally {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public ExchangeRateDTO update(String baseCode, String targetCode, BigDecimal newRate) throws InterruptedException,
            MyException, SQLException, IllegalAccessException, NotFoundException {
        Connection conn = connectionPool.take();
        try{
            conn.setAutoCommit(false);

            if(newRate.compareTo(BigDecimal.ZERO)==-1){
                throw new MyException("rate cant be less than 0");
            }
            ExchangeRateDao erDao = new ExchangeRateDao(conn);
            ExchangeRate entity = erDao.getByCodeCombo(baseCode, targetCode);
            if(entity.getId()==0){
                throw new NotFoundException("exchange rate "+baseCode+targetCode+" does not exist");
            }
            entity.setRate(newRate);
            erDao.update(entity);
            ExchangeRate updatedEntity = erDao.getByCodeCombo(baseCode, targetCode);

            CurrencyDao currencyDao = new CurrencyDao(conn);
            Currency baseCurrency = currencyDao.getByCode(baseCode);
            Currency targetCurrency = currencyDao.getByCode(targetCode);

            ExchangeRateDTO exchangeRateDTO = new ExchangeRateDTO.Builder()
                    .setId(updatedEntity.getId())
                    .setRate(updatedEntity.getRate())
                    .setBaseCurrency(baseCurrency)
                    .setTargetCurrency(targetCurrency).build();
            conn.commit();
            return exchangeRateDTO;
        }
        catch (Exception ex){
            try {
                conn.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            ex.printStackTrace();
            throw ex;
        }
        finally {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
