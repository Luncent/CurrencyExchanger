package Services;

import Dao.ConnectionPoolManager;
import Dao.CurrencyDao;
import Entities.Currency;
import Exceptions.MyException;
import Exceptions.NotFoundException;
import Exceptions.RowExists;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class CurrencyService {
    private final String URL;
    private final ConnectionPoolManager connectionPoolManager;
    public CurrencyService(ConnectionPoolManager connectionPoolManager, String URL){
        this.connectionPoolManager = connectionPoolManager;
        this.URL = URL;
    }

    public List<Currency> getAll() throws SQLException, InterruptedException, InstantiationException, IllegalAccessException {
        try(Connection conn = connectionPoolManager.getConnection()){
            System.out.println("currency checking autocommit: "+conn.getAutoCommit());
            CurrencyDao dao = new CurrencyDao(conn);
            return dao.selectAll();
        }
        catch (Exception ex){
            ex.printStackTrace();
            throw ex;
        }
    }

    public Currency getByCode(String code) throws SQLException, InterruptedException, NotFoundException {
        try(Connection conn = connectionPoolManager.getConnection()){
            CurrencyDao dao = new CurrencyDao(conn);
            Currency currency = dao.getByCode(code);
            if(currency.getId()==0){
                throw new NotFoundException("currency not found");
            }
            else{
                return currency;
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
            throw ex;
        }
    }

    public Currency add(Currency newCurrency) throws InterruptedException,
            SQLException, IllegalAccessException, MyException, RowExists {

        Connection conn = connectionPoolManager.getConnection();
        try{
            conn.setAutoCommit(false);
            CurrencyDao dao = new CurrencyDao(conn);
            if(!Currency.checkMock(dao.getByCode(newCurrency.getCode()))){
                throw new RowExists("currency exists");
            }
            dao.insert(newCurrency);
            Currency currency = dao.getByCode(newCurrency.getCode());
            conn.commit();
            return currency;
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
