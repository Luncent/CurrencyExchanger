package Dao;

import java.sql.SQLException;
import java.util.List;

public interface CRUD <T> { // you can call it a dao, per pattern name
    public List<T> selectAll() throws SQLException, InstantiationException, IllegalAccessException;
    public int insert(T obj) throws SQLException, IllegalAccessException;
    public T getByID(int id) throws SQLException, InstantiationException, IllegalAccessException;
    public int update(T obj) throws SQLException, IllegalAccessException;
    public int delete(T obj) throws SQLException, IllegalAccessException;
}
