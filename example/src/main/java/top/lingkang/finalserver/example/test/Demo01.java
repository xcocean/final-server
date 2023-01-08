package top.lingkang.finalserver.example.test;

import cn.hutool.core.io.FileUtil;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import top.lingkang.finalserver.server.FinalServerApplication;
import top.lingkang.finalserver.server.annotation.Controller;
import top.lingkang.finalserver.server.annotation.FinalServerBoot;
import top.lingkang.finalserver.server.annotation.GET;
import top.lingkang.finalserver.server.annotation.POST;
import top.lingkang.finalserver.server.web.entity.ResponseFile;
import top.lingkang.finalserver.server.web.http.*;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * @author lingkang
 * Created by 2022/12/6
 */
@FinalServerBoot
@Controller
public class Demo01 {
    public static void main(String[] args) {
        FinalServerApplication.run(Demo01.class, args);
    }

    private int count = 0;

    @GET
    public void index(HttpResponse response, String name, Request request) {
        // response.returnString("hi你好啊");
        // System.out.println(FinalServerHttpContext.getRequest().requestId());
//        System.out.println(request.getCookies());
        System.out.println(request.getSession().getAttribute("vv"));
        request.getSession().setAttribute("vv", "你好啊666");
        HashMap<String, Object> map = new HashMap<>();
        map.put("vv","直接输出模板渲染的变量vv");
        response.returnTemplate("index",map);
//        count++;
//        System.out.println(count);
    }

    @GET("/w")
    public void w(HttpResponse response) {
        response.returnTemplate("ws.html");
    }

    @GET("/test")
    public void test() throws Exception {
        FinalServerApplication.addRequestHandler("/a", RequestMethod.GET, context -> {
            System.out.println(context.getRequest().getParam("name"));
            context.getResponse().returnString("自定义custom");
        });
    }

    @GET("/c")
    public void cookie(FinalServerContext context) throws Exception {
        System.out.println(context.getRequest().getSession().getAttribute("vv"));
        Set<Cookie> cookies = context.getRequest().getCookies();
        for (Cookie cookie : cookies)
            System.out.println(cookie.name() + "   " + cookie.value());
        // 添加cookie
        DefaultCookie lk = new DefaultCookie("lk", System.currentTimeMillis() + "");
        lk.setMaxAge(666);
        lk.setPath("/");
        context.getResponse().addCookie(lk);
    }

    @GET("/r")
    public void r(HttpResponse response) {
        response.sendRedirect("");
    }

    @GET("t")
    public void t(HttpResponse response) {
        response.returnString("index");
    }

    @GET("s")
    public String s(HttpResponse response, HttpRequest request) {
        request.getSession().setAttribute("time", new Date());
        return "session";
    }

    @GET("s1")
    public Object s1(HttpRequest request) {
        return request.getSession().getAttribute("time");
    }

    @POST("/p")
    public Object p(Request request) throws IOException {
        List<MultipartFile> upload = request.getFileUpload();
        MultipartFile file = upload.get(0);

        System.out.println(file.getParamName());// 上传文件的参数名
        System.out.println(file.getFileName());// 名称
        System.out.println(file.getLength());// 长度
        System.out.println(file.getFile()); // 文件对象
        System.out.println(file);
        return "ok";
    }

    @POST("/p2")
    public Object p2(Request request) throws IOException {
        System.out.println(request.getPath());
        return "p2";
    }

    @POST("/a")
    public Object a(String a) {
        return a;
    }

    @GET("/d")
    public Object d(HttpResponse response) {
        response.returnFile(new ResponseFile("C:\\Users\\lingkang\\Desktop\\1.xml").setDownload(true));
        return "ok";
    }

    @GET("/d2")
    public Object d2(HttpResponse response) throws Exception {
        // 当数据为二进制时，先将数据转为临时文件，再写入，并设置delete
        byte[] zipByte = new byte[]{1, 2, 3, 4};
        File tempFile = File.createTempFile("123前缀", ".zip");// 后缀有.
        FileUtil.writeBytes(zipByte, tempFile);
        // 返回下载文件并响应后删除临时文件
        response.returnFile(new ResponseFile(tempFile.getPath()).setDownload(true).setDelete(true));
        return "ok";
    }

    @GET("/index")
    public void index(FinalServerContext context){
        // 添加值到session中
        context.getRequest().getSession().setAttribute("sessionValue","这是session值");

        HashMap<String, Object> map = new HashMap<>();
        map.put("vv","vvvvvvvvv");// 直接输出模板渲染的变量
        context.getResponse().returnTemplate("index.html",map);

        // context.getResponse().returnTemplate("index.html");
    }
}
