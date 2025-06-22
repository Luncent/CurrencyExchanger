package Dao;

import Annotations.Column;
import Annotations.Id;
import Annotations.Table;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class CRUDImpl<T> implements CRUD<T>{
    private static final String SELECT_ALL = "SELECT * FROM %s";
    private static final String INSERT = "INSERT INTO %s%s \nVALUES %s";
    private static final String SELECT_BYID = "SELECT * FROM %s WHERE %s = ?";
    private static final String UPDATE = "UPDATE %s SET \n %s WHERE %s=?";
    Connection conn;
    Class clazz;
    public CRUDImpl(Class clazz, Connection conn){
        this.clazz = clazz;
        this.conn = conn;
    }

    private String getTableName(){
        Table tableAnnot =(Table)clazz.getAnnotation(Table.class);
        return tableAnnot == null ? clazz.getSimpleName() :
                tableAnnot.name() != null ? tableAnnot.name() : clazz.getSimpleName();
    }

    private void fillEntity(Field[] fields, ResultSet rs, T entity) throws SQLException, InstantiationException, IllegalAccessException {
        for (int i = 0; i < fields.length; i++) {
            fields[i].setAccessible(true);
            if (fields[i].getType() == BigDecimal.class) {
                fields[i].set(entity, rs.getBigDecimal(i + 1));
            } else {
                fields[i].set(entity, rs.getObject(i + 1));
            }
        }
    }

    @Override
    public List<T> selectAll() throws SQLException, InstantiationException, IllegalAccessException {
        String tableName = getTableName();
        String SQL = String.format(SELECT_ALL, tableName);
        System.out.println(SQL);

        try(PreparedStatement stmt = conn.prepareStatement(SQL)) {
            ResultSet rs = stmt.executeQuery();
            List<T> entities = new ArrayList<>();

            Field[] fields = clazz.getDeclaredFields();
            while(rs.next()) {
                T entity = (T)clazz.newInstance();
                fillEntity(fields,rs,entity);
                entities.add(entity);
            }
            return entities;
        }
    }

    @Override
    public int insert(T obj) throws SQLException, IllegalAccessException {
        String tableName = getTableName();

        Field[] fields = clazz.getDeclaredFields();
        List<Field> fieldsWithoutId = Arrays.stream(fields).filter(x->x.getAnnotation(Id.class)==null)
                .collect(Collectors.toCollection(ArrayList::new));

        String columns = fieldsWithoutId.stream()
                .filter(x->x.getAnnotation(Id.class)==null)
                .map(x->x.getAnnotation(Column.class)==null ? x.getName() : x.getAnnotation(Column.class).name())
                .collect(Collectors.joining(",","(",")"));

        String questionMarks = fieldsWithoutId.stream()
                .filter(x->x.getAnnotation(Id.class)==null)
                .map(x->"?")
                .collect(Collectors.joining(",","(",")"));
        String SQL = String.format(INSERT,tableName,columns,questionMarks);
        System.out.println(SQL);
        try(PreparedStatement stmt = conn.prepareStatement(SQL)){
            for(int i = 0; i<fieldsWithoutId.size(); i++){
                fieldsWithoutId.get(i).setAccessible(true);
                stmt.setObject(i+1,fieldsWithoutId.get(i).get(obj));
            }
            return stmt.executeUpdate();
        }
    }

    @Override
    public T getByID(int id) throws SQLException, InstantiationException, IllegalAccessException {
        T entity = (T)clazz.newInstance();

        String tableName = getTableName();

        Field[] fields = clazz.getDeclaredFields();
        if(fields[0].getAnnotation(Id.class)==null){
            throw new IllegalArgumentException("first field of entity must be @Id");
        }
        String key = fields[0].getAnnotation(Column.class) == null ? fields[0].getName()
                :  fields[0].getAnnotation(Column.class).name();
        String SQL = String.format(SELECT_BYID,tableName,key);
        System.out.println(SQL);
        try(PreparedStatement stmt = conn.prepareStatement(SQL)){
            stmt.setObject(1,id);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                fillEntity(fields,rs,entity);
            }
            return entity;
        }
    }

    @Override
    public int update(T obj) throws SQLException, IllegalAccessException {
        String tableName = getTableName();

        Field[] fields = clazz.getDeclaredFields();
        Field idField = fields[0];
        if(idField.getAnnotation(Id.class)==null){
            throw new IllegalArgumentException("first field of entity must be @Id");
        }
        String id = fields[0].getAnnotation(Column.class) == null ?
                fields[0].getName() : fields[0].getAnnotation(Column.class).name();

        String columns = Arrays.stream(fields).filter(x->x.getAnnotation(Id.class)==null)
                .map(x-> {
                    String col = x.getAnnotation(Column.class) == null ? x.getName() : x.getAnnotation(Column.class).name();
                    return col+" = ?\n";
                })
                .collect(Collectors.joining(",","",""));

        String SQL = String.format(UPDATE,tableName,columns,id);
        System.out.println(SQL);

        try(PreparedStatement stmt = conn.prepareStatement(SQL)){
            idField.setAccessible(true);
            for(int i = 1; i< fields.length; i++){
                fields[i].setAccessible(true);
                stmt.setObject(i,fields[i].get(obj));
            }
            stmt.setObject(fields.length,idField.get(obj));
            return stmt.executeUpdate();
        }
    }

    @Override
    public int delete(T obj) throws SQLException, IllegalAccessException {
        String tableName = getTableName();

        Field[] fields = clazz.getDeclaredFields();
        if(fields[0].getAnnotation(Id.class)==null){
            throw new IllegalArgumentException("first field of entity must be @Id");
        }
        String key = fields[0].getAnnotation(Column.class) == null ? fields[0].getName()
                :  fields[0].getAnnotation(Column.class).name();
        String SQL = String.format("DELETE FROM %s WHERE %s = ?",tableName,key);
        System.out.println(SQL);
        try(PreparedStatement stmt = conn.prepareStatement(SQL)){
            fields[0].setAccessible(true);
            stmt.setObject(1,fields[0].get(obj));
            return stmt.executeUpdate();
        }
    }
}
