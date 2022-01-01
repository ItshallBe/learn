package com.zzx.helper;

import com.sun.org.apache.bcel.internal.generic.LOOKUPSWITCH;
import com.zzx.util.CollectionUtil;
import com.zzx.util.PropsUtil;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;


/* 数据库操作助手类 */
public class DatabaseHelper {
    private static final QueryRunner QUERY_RUNNER;
    private static final ThreadLocal<Connection> CONNECTION_HOLDER;
    private static final BasicDataSource DATA_SOURCE;

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseHelper.class);
    static {
        CONNECTION_HOLDER = new ThreadLocal<Connection>();
        QUERY_RUNNER = new QueryRunner();
        DATA_SOURCE = new BasicDataSource();
        Properties conf = PropsUtil.loadProps("config.properties");
        String driver = conf.getProperty("jdbc.driver");
        String url = conf.getProperty("jdbc.url");
        String username = conf.getProperty("jdbc.username");
        String password = conf.getProperty("jdbc.password");
        DATA_SOURCE.setDriverClassName(driver);
        DATA_SOURCE.setUrl(url);
        DATA_SOURCE.setUsername(username);
        DATA_SOURCE.setPassword(password);
    }
    /* 获取数据库连接 */
    public static Connection getConnection() {
        Connection conn = CONNECTION_HOLDER.get();
        try {
            conn = DATA_SOURCE.getConnection();
            //conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            LOGGER.error("get connection failure", e);
            throw new RuntimeException();
        } finally {
            CONNECTION_HOLDER.set(conn);
        }
        return conn;
    }
    /* 关闭数据库连接 */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                LOGGER.error("close connection failure", e);
            }
        }
    }
    /* ThreadLocal 关闭数据库连接
    * 优化一：引入dbcp2池化数据库连接后，每次sql访问不必关闭连接了
    * */
    public static void closeConnection() {
        Connection conn = CONNECTION_HOLDER.get();
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                LOGGER.error("close connection failure", e);
                throw new RuntimeException(e);
            } finally {
                CONNECTION_HOLDER.remove();
            }
        }
    }
    /* 查询实体类 */
    public static <T> List<T> queryEntityList(Class<T>entityClass, Connection conn, String sql, Object... parms){
        List<T>entityList;
        try {
            entityList = QUERY_RUNNER.query(conn, sql, new BeanListHandler<T>(entityClass), parms);
        } catch (SQLException e) {
            LOGGER.error("query entity list failure", e);
            throw new RuntimeException(e);
        }
        return entityList;
    }
    /* ThreadLocal查询实体List */
    public static <T> List<T> queryEntityList(Class<T>entityClass, String sql, Object... parms){
        List<T>entityList;
        try {
            Connection conn = getConnection();
            entityList = QUERY_RUNNER.query(conn, sql, new BeanListHandler<T>(entityClass), parms);
        } catch (SQLException e) {
            LOGGER.error("query entity list failure", e);
            throw new RuntimeException(e);
        } finally {
            closeConnection();
        }
        return entityList;
    }
    /* ThreadLocal查询单个实体 */
    public static<T> T queryEntity(Class<T>entityClass, String sql, Object... parms) {
        T entity;
        try {
            Connection conn = getConnection();
            entity = QUERY_RUNNER.query(conn, sql, new BeanHandler<T>(entityClass), parms);
        } catch (SQLException e) {
            LOGGER.error("query entity failure", e);
            throw new RuntimeException();
        } finally {
            closeConnection();
        }
        return entity;
    }
    /* ThreadLocal连接查询 */
    public static List<Map<String, Object>> executeQuery(String sql, Object... parms) {
        List<Map<String, Object>> result;
        try {
            Connection conn = getConnection();
            result = QUERY_RUNNER.query(conn, sql, new MapListHandler(), parms);
        } catch (SQLException e) {
            LOGGER.error("execute query failure", e);
            throw new RuntimeException();
        }
        return result;
    }
    /* 执行更新语句（包括update, insert, delete) */
    public static int executeUpdate(String sql, Object... parms) {
        int rows = 0;
        try {
            Connection conn = getConnection();
            rows = QUERY_RUNNER.update(conn, sql, parms);
        } catch (SQLException e) {
            LOGGER.error("execute update failure", e);
            throw new RuntimeException();
        } finally {
            closeConnection();
        }
        return rows;
    }
    /* 插入实体 */
    public static <T> boolean insertEntity(Class<T>entityClass, Map<String, Object>fieldMap) {
        if (CollectionUtil.isEmpty(fieldMap)) {
            LOGGER.error("can not insert entity: fieldMap is emtpy");
            return false;
        }

        /* INSERT INTO demo (col1, col2, ...) VALUES (val1, val2, ...) */
        String sql = "INSERT INTO " + getTableName(entityClass);
        StringBuilder colums = new StringBuilder("(");
        StringBuilder values = new StringBuilder("(");
        for (String fieldName : fieldMap.keySet()) {
            colums.append(fieldName).append(", ");
            values.append("?, ");
        }
        colums.replace(colums.lastIndexOf(", "), colums.length(), ")");
        values.replace(values.lastIndexOf(", "), values.length(), ")");
        sql += colums + " VALUES " +values;
        Object[] parms = fieldMap.values().toArray();
        return executeUpdate(sql, parms) == 1;
    }
    /* 更新实体 */
    public static <T> boolean updateEntity(Class<T> entityClass, long id, Map<String, Object> fieldMap) {
        if (CollectionUtil.isEmpty(fieldMap)) {
            LOGGER.error("can not update entity: fieldMap is empty");
            return false;
        }

        /* UPDATE demo SET col1 = xx, col2 = xx, ...*/
        String sql = "UPDATE " + getTableName(entityClass) + " SET ";
        StringBuilder columns = new StringBuilder();
        for (String fieldName : fieldMap.keySet()) {
            columns.append(fieldName).append("=?, ");
        }
        sql += columns.substring(0, columns.lastIndexOf(", ")) + " WHERE id=?";
        List<Object> parmList = new ArrayList<Object>();
        parmList.addAll(fieldMap.values());
        parmList.add(id);
        Object[] parms = parmList.toArray();

        return executeUpdate(sql, parms) == 1;
    }
    /* 删除实体 */
    public static <T> boolean deleteEntity(Class<T> entityClass, long id) {
        String sql = "DELETE FROM " + getTableName(entityClass) + " WHERE id=?";
        return executeUpdate(sql, id) == 1;
    }
    /* 执行sql文件 */
    public static void executeSqlFile(String filePath) {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        try {
            String sql;
            while ((sql = reader.readLine()) != null) {
                executeUpdate(sql);
            }
        } catch (Exception e) {
            LOGGER.error("execute sql file failure", e);
            throw new RuntimeException(e);
        }
    }
    private static String getTableName(Class<?>entityClass) {
        /* entityClass.getSimpleName()是Customer，需要转为customer */
        return entityClass.getSimpleName().toLowerCase();
    }
}
