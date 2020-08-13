package dao;


import java.sql.Connection;
import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// 做好获取数据库连接的准备
public class DBUtil {
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/tangshi?characterEncoding=utf8&useSSL=false";
    private static final String USERNAME="root";
    private static final String PASSWORD="123456";

    private static volatile DataSource dataSource=null;

    public static DataSource getDataSource(){
        // 通过这个方法来创建 DataSource 的实例
        if(dataSource==null){
            synchronized (DBUtil.class){
                if(dataSource==null){
                    dataSource=new MysqlDataSource();
                    MysqlDataSource tmpDataSource=(MysqlDataSource)dataSource;
                    tmpDataSource.setURL(URL);
                    tmpDataSource.setUser(USERNAME);
                    tmpDataSource.setPassword(PASSWORD);
                }
            }
        }
        return dataSource;
    }

    // 获取连接
    public static Connection getConnection(){
        try {
            return getDataSource().getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 关闭连接
    public void close(Connection connection, PreparedStatement statement, ResultSet resultSet){
        try {
            if (connection != null) {
                connection.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (resultSet == null) {
                resultSet.close();
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}
