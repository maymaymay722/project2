package api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dao.Image;
import dao.ImageDao;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ImageServlet extends HttpServlet {

    /*
    上传图片
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 1. 获取图片的属性信息，并且存入数据库
        // a) 需要创建一个 factory 对象 和 upload 对象，为了获取图片属性（固定逻辑）
        FileItemFactory factory=new DiskFileItemFactory();
        ServletFileUpload upload=new ServletFileUpload(factory);

        // b) 通过 upload 对象进一步解析请求（解析 HTTP 请求中 body 的内容）
        // FileItem 代表一个上传支持的对象
        // 理论上来说，HTTP 支持一个请求同时上传多个文件
        List<FileItem> items=null;
        try{
            items=upload.parseRequest(req);
        } catch (FileUploadException e) {
            // 出现异常说明解析出错
            e.printStackTrace();

            // 告诉客户端出现的具体的错误是啥（构造的 HTTP 请求、响应）（一个 JSON 格式的字符串）
            // 对于 JSON 格式的响应，需要把 conten-type 设定成指定格式
            resp.setContentType("application/json;charset=utf-8");
            // 双引号之前要添加转义字符  “\”
            resp.getWriter().write("{ \"ok\": false, \"reason\": \"请求解析失败\" }");
            return;
        }

        // c) 把 FileItem 中的属性提取出来，转换成 Image 对象，才能存到数据库中
        // 当前只考虑一张图片的情况
        FileItem fileItem=items.get(0);
        Image image=new Image();
        image.setImageName(fileItem.getName());
        image.setSize((int) fileItem.getSize());
        // 手动获取一下当前日期，并转成格式日期，yyyyMMdd => 20200808
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyyMMdd");
        image.setUploadTime(simpleDateFormat.format(new Date()));
        image.setContentType(fileItem.getContentType());

        // 构造一个路径来保存（图片保存路径：./image/文件名）
        // 如果有两个图片，内容不同，但是名字相同，此时就可能出现上传失败的情况
        // 解决办法：让每次上传的图片对应的路径都不相同（在路径中加上时间戳）
        //image.setPath("./image"+System.currentTimeMillis()+"_"+image.getImageName());

        // Md5 计算过程
        image.setMd5(DigestUtils.md5Hex(fileItem.get()));
        image.setPath("./image"+image.getMd5());

        // 存到数据库中
        ImageDao imageDao=new ImageDao();
        // 看看数据库中是否存在相同的 Md5 值得图片，如果不存在，返回 null
        Image existImage=imageDao.selectByMd5(image.getMd5());
        imageDao.insert(image);


        // 2. 获取图片的内容信息，并且写入磁盘文件
        if(existImage==null){
            File file=new File(image.getPath());
            try{
                fileItem.write(file);
            } catch (Exception e) {
                e.printStackTrace();

                resp.setContentType("application/json;charset=utf-8");
                resp.getWriter().write("{ \"ok\": false, \"reason\": \"写磁盘失败\" }");
                return;
            }
        }


        // 3. 给客户端返回一个结果数据
        // 上传完图片之后，直接跳转到 index.html
        resp.sendRedirect("index.html");

        //resp.setContentType("application/json;charset=utf-8");
        //resp.getWriter().write("{ \"ok\": true }");
    }

    /*
    查看图片属性：既能查看所有图片，也能查看指定图片
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // req 对象中包含了请求中的所有信息
        // resp 对象要生成的结果就放到里面去
        // doGet 方法就是要根据请求，生成响应

        // 考虑到查看所有图片属性和查看指定图片属性
        // 通过 URL 中带有 imageId 参数来进行区分
        // 存在 imageId 查看指定图片属性，否则就查看所有图片属性
        // 通过 getParameter 方法，可以获得 URL 后面的属性
        String imageId=req.getParameter("imageId");
        if(imageId==null || imageId.equals("")){
            // 查看所有图片属性
            selectAll(req,resp);
        }else{
            // 查看指定图片属性
            selectOne(imageId,resp);
        }
    }

    private void selectOne(String imageId, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=utf-8");

        // 1. 创建 ImageDao 对象，并查找数据库
        ImageDao imageDao=new ImageDao();
        Image image=imageDao.selectOne(Integer.parseInt(imageId));
        // 2. 使用 gson 把查到的数据转换成 json 格式，并写回给响应对象
        Gson gson=new GsonBuilder().create();
        String jsonData=gson.toJson(image);
        resp.getWriter().write(jsonData);
    }

    private void selectAll(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=utf-8");

        // 1. 创建 ImageDao 对象，并查找数据库
        ImageDao imageDao=new ImageDao();
        List<Image> images=imageDao.selectAll();
        // 2. 把查找到的结果转成 JSON 格式的字符串，并且写回给 resp 对象
        Gson gson=new GsonBuilder().create();
        // jsonData 就是一个 JSON 格式的字符串，与之前约定的一样
        // 核心代码，gson 自动完成了大量的格式转换工作
        // 只要把之前的相关代码的字段都约定成统一的命名，下面的操作就可以一步到位的完成整个转换
        String jsonData=gson.toJson(images);
        resp.getWriter().write(jsonData);
    }

    /*
    删除指定图片
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=utf-8");
        // 1. 先获取到请求中的 imageId
        String imageId=req.getParameter("imageId");
        if(imageId==null || imageId.equals("")){
            resp.setStatus(200);
            resp.getWriter().write("{ \"ok\": false, \"reason\": \"解析请求失败\" }");
            return;
        }

        // 2. 创建 ImageDao 对象，查看到该图片对象对应的相关属性（知道这个图片对应的文件路径）
        ImageDao imageDao=new ImageDao();
        Image image=imageDao.selectOne(Integer.parseInt(imageId));
        if(image==null){
            // 此时请求中传入的 Id 在数据库中不存在
            resp.setStatus(200);
            resp.getWriter().write("{ \"ok\": false, \"reason\": \"imageId 在数据库中不存在\" }");
            return;
        }

        // 3. 删除数据库中的记录
        imageDao.delete(Integer.parseInt(imageId));

        // 4. 删除本地磁盘文件
        File file=new File(image.getPath());
        file.delete();
        resp.setStatus(200);
        resp.getWriter().write("{ \"ok\": true }");
    }
}
