package dao;

import common.JavaImageServerException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/*
Image 对象的管理器，借助这个类完成 Image 对象的增删改查操作
 */
public class ImageDao {
    /*
    把 image 对象插入到数据库中
     */
    public void insert(Image image){
        // 1. 获取数据库连接
        Connection connection=DBUtil.getConnection();
        // 2. 创建并拼装 SQL 语句
        String sql="insert into image_table values(null,?,?,?,?,?,?)";
        PreparedStatement statement=null;
        try {
            statement=connection.prepareStatement(sql);
            statement.setString(1,image.getImageName());
            statement.setInt(2,image.getSize());
            statement.setString(3,image.getUploadTime());
            statement.setString(4,image.getContentType());
            statement.setString(5,image.getPath());
            statement.setString(6,image.getMd5());

            // 3. 执行 SQL 语句
            int ret=statement.executeUpdate();
            if(ret!=1){
                // 程序出现问题，抛出一个异常
                throw new JavaImageServerException("插入数据库错误！");
            }
        } catch (SQLException |JavaImageServerException e) {
            e.printStackTrace();
        }finally {
            // 4. 关闭连接和 Statement 对象
            DBUtil.close(connection,statement,null);
        }
    }

    /*
    查找数据库中的所有图片的信息
     */
    public List<Image> selectAll(){
        List<Image> images=new ArrayList<>();
        // 1. 获取数据库连接
        Connection connection = DBUtil.getConnection();
        // 2. 构造 SQL 语句
        String sql="select * from image_table";
        PreparedStatement statement=null;
        ResultSet resultSet=null;
        try {
            // 3. 执行 SQL 语句
            statement=connection.prepareStatement(sql);
            resultSet=statement.executeQuery();
            // 4. 处理结果集
            while(resultSet.next()){   // 如果有 resultSet，就获取到当前内容
                Image image=new Image();
                image.setImageId(resultSet.getInt("imageId"));
                image.setImageName(resultSet.getString("imageName"));
                image.setSize(resultSet.getInt("size"));
                image.setUploadTime(resultSet.getString("uploadTime"));
                image.setContentType(resultSet.getString("contentType"));
                image.setPath(resultSet.getString("path"));
                image.setMd5(resultSet.getString("md5"));
                images.add(image);
            }
            return images;
        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
            // 5. 关闭连接
            DBUtil.close(connection,statement,resultSet);
        }
        return null;   // 出现异常，则返回null
    }

    /*
    根据 imageId 查找指定的图片信息
     */
    public Image selectOne(int imageId){
        // 1. 获取数据库连接
        Connection connection=DBUtil.getConnection();
        // 2. 构造 SQL 语句
        String sql="select * from image_table where imageId=?";
        PreparedStatement statement=null;
        ResultSet resultSet=null;
        try {
            // 3. 执行 SQL 语句
            statement=connection.prepareStatement(sql);
            statement.setInt(1,imageId);
            resultSet=statement.executeQuery();

            // 4. 处理结果集    (要么只有一个，要么一个都没有)
            while(resultSet.next()){
                Image image=new Image();
                image.setImageId(resultSet.getInt("imageId"));
                image.setImageName(resultSet.getString("imageName"));
                image.setSize(resultSet.getInt("size"));
                image.setUploadTime(resultSet.getString("uploadTime"));
                image.setContentType(resultSet.getString("contentType"));
                image.setPath(resultSet.getString("path"));
                image.setMd5(resultSet.getString("md5"));
                return image;
            }
        }catch(SQLException e){
            e.printStackTrace();
        }finally {
            // 5. 关闭连接
            DBUtil.close(connection, statement, resultSet);
        }
        return null;
    }

    /*
    根据 imageId 删除指定的图片
     */
    public void delete(int imageId){
        // 1. 获取数据库连接
        Connection connection=DBUtil.getConnection();
        // 2. 构造 SQL 语句
        String sql="delete from image_table where imageId=?";
        PreparedStatement statement=null;
        try {
            // 3. 执行 SQL 语句
            statement=connection.prepareStatement(sql);
            statement.setInt(1,imageId);
            int ret=statement.executeUpdate();
            if(ret != 1){
                throw new JavaImageServerException("删除数据库操作失败");
            }
        }catch(SQLException |JavaImageServerException e){
            e.printStackTrace();
        }finally {
            // 4. 关闭连接
            DBUtil.close(connection,statement,null);
        }
    }

    public Image selectByMd5(String md5){
        // 1. 获取数据库连接
        Connection connection=DBUtil.getConnection();
        // 2. 构造 SQL 语句
        String sql="select * from image_table where md5=?";
        PreparedStatement statement=null;
        ResultSet resultSet=null;
        try {
            // 3. 执行 SQL 语句
            statement=connection.prepareStatement(sql);
            statement.setString(1,md5);
            resultSet=statement.executeQuery();

            // 4. 处理结果集
            while(resultSet.next()){
                Image image=new Image();
                image.setImageId(resultSet.getInt("imageId"));
                image.setImageName(resultSet.getString("imageName"));
                image.setSize(resultSet.getInt("size"));
                image.setUploadTime(resultSet.getString("uploadTime"));
                image.setContentType(resultSet.getString("contentType"));
                image.setPath(resultSet.getString("path"));
                image.setMd5(resultSet.getString("md5"));
                return image;
            }
        }catch(SQLException e){
            e.printStackTrace();
        }finally {
            // 5. 关闭连接
            DBUtil.close(connection, statement, resultSet);
        }
        return null;
    }

    //public static void main(String[] args) {
        // 用于进行简单的测试
        // 1. 测试插入数据
        //Image image=new Image();
        //image.setImageName("1.png");
        //image.setSize(100);
        //image.setUploadTime("20200512");
        //image.setContentType("image/png");
        //image.setPath("./data/1.png");
        //image.setMd5("11223344");
        //ImageDao imageDao=new ImageDao();
        //imageDao.insert(image);

        // 2. 测试查找所有图片信息
        //ImageDao imageDao=new ImageDao();
        //List<Image> images=imageDao.selectAll();
        //System.out.println(images);

        // 3. 测试查找指定图片信息
        //ImageDao imageDao=new ImageDao();
        //Image image=imageDao.selectOne(23);
        //System.out.println(image);

        // 4. 测试删除图片
        //ImageDao imageDao=new ImageDao();
        //imageDao.delete(23);
    //}
}
