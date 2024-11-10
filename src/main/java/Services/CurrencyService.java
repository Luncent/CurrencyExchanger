package Services;

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
    private final String URL; // unused?
    private final BlockingQueue<Connection> connectionPool;
    public CurrencyService(BlockingQueue<Connection> connectionPool, String URL){
        this.connectionPool = connectionPool;
        this.URL = URL;
    }

    public List<Currency> getAll() throws SQLException, InterruptedException, InstantiationException, IllegalAccessException {
        try(Connection conn = connectionPool.take()){
            CurrencyDao dao = new CurrencyDao(conn);
            return dao.selectAll();
        }
        catch (Exception ex){
            ex.printStackTrace();
            throw ex;
        }
    }

    public Currency getByCode(String code) throws SQLException, InterruptedException, NotFoundException {
        try(Connection conn = connectionPool.take()){ 
            CurrencyDao dao = new CurrencyDao(conn);
            Currency currency = dao.getByCode(code);
            if(currency.getId()==0){ // dao should return Optional.empty for not found case
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

        Connection conn = connectionPool.take();
        try{
            // ideally Service just carries out the business logic. 
            // But for you to have > 1 operation in trx may make sense to leave as-is.
            // I would make dao methods static then not to re-create dao objects all the time.
            conn.setAutoCommit(false);
            CurrencyDao dao = new CurrencyDao(conn);
            if(!Currency.checkMock(dao.getByCode(newCurrency.getCode()))){
                throw new RowExists("currency exists");
            }
            dao.insert(newCurrency);
            Currency currency = dao.getByCode(newCurrency.getCode()); // will this work with commit in next line?
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
                conn.close(); // why to close, if you use connection pool?
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
