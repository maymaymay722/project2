package dao;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


/*
封装获取数据库连接的操作
 */
public class DBUtil {
    private static final String URL="jdbc:mysql://127.0.0.1:3306/java_image_server?characterEncoding=utf8&useSSL=true";
    private static final String USERNAME="root";
    private static final String PASSWORD="123456";

    private static volatile DataSource dataSource=null;

    public static DataSource getDataSource(){
        // 通过这个方法来创建 DataSource 的实例
        // 线程安全：（1）加锁   （2）双重判定   （3）volatile
        if(dataSource==null) {
            synchronized (DBUtil.class) {
                if (dataSource == null) {
                    dataSource = new MysqlDataSource();

                    // 把之前创建的 URL、USERNAME、PASSWORD 设定进去
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
    public static Connection getConnection() {
        try {
            return getDataSource().getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 关闭连接
    public static void close(Connection connection, PreparedStatement statement, ResultSet resultSet){
        // 先创建的后关闭
        try {
            if (connection != null) {
                connection.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (resultSet != null) {
                resultSet.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
