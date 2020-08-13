package dao;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import java.util.List;
import static org.junit.Assert.*;

public class ImageDaoTest {
    @Before
    public void before(){
        System.out.println("--------before--------");
    }

    @After
    public void after(){
        System.out.println("--------after--------");
    }

    @Ignore
    @Test
    public void insert() {
        Image image=new Image();
        image.setImageName("1.png");
        image.setSize(100);
        image.setUploadTime("20200808");
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
        System.out.println("size = "+images.size());
        System.out.println();
        for (Image image:images) {
            System.out.println("name = "+image.getImageName());
        }
    }

    @Test
    public void selectOne() {
        ImageDao imageDao=new ImageDao();
        Image image=imageDao.selectOne(8);

        System.out.println(image.getImageName());
        System.out.println(image.getImageId());
        System.out.println(image.getMd5());
        System.out.println(image.getPath());
    }

    @Ignore
    @Test
    public void delete() {
        ImageDao imageDao=new ImageDao();
        imageDao.delete(1);
    }

    @Test
    public void selectByMd5() {
        ImageDao imageDao=new ImageDao();
        String md5="0a0f533665cf8e6ae6d6512b97010c48";
        Image image=imageDao.selectByMd5(md5);
        System.out.println("name = "+image.getImageName());
    }
}