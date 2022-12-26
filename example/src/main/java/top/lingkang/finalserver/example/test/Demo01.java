package top.lingkang.finalserver.example.test;

import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import top.lingkang.finalserver.server.FinalServerApplication;
import top.lingkang.finalserver.server.annotation.Controller;
import top.lingkang.finalserver.server.annotation.FinalServerBoot;
import top.lingkang.finalserver.server.annotation.GET;
import top.lingkang.finalserver.server.annotation.POST;
import top.lingkang.finalserver.server.web.http.*;

import java.util.Date;
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
        request.getSession().setAttribute("vv", "你好啊666");
        response.returnTemplate("index.html");
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
    public Object s1(HttpRequest request){
        return request.getSession().getAttribute("time");
    }

    @POST("/p")
    public Object p(String name){
        return "post: "+ name;
    }
}
