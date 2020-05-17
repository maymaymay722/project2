package dao;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class ImageDaoTest {

    @Test
    public void insert() {
        // 1. 测试插入数据
        Image image=new Image();
        image.setImageName("1.png");
        image.setSize(100);
        image.setUploadTime("20200512");
        image.setContentType("image/png");
        image.setPath("./data/1.png");
        image.setMd5("11223344");
        ImageDao imageDao=new ImageDao();
        imageDao.insert(image);
    }

    @Test
    public void selectAll() {
        ImageDao imageDao=new ImageDao();
        List<Image> images=imageDao.selectAll();
        System.out.println(images);
    }

    @Test
    public void selectOne() {
        ImageDao imageDao=new ImageDao();
        Image image=imageDao.selectOne(23);
        System.out.println(image);
    }

    @Test
    public void delete() {
        ImageDao imageDao=new ImageDao();
        imageDao.delete(23);
    }
}