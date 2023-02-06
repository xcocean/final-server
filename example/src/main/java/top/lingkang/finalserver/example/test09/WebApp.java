package top.lingkang.finalserver.example.test09;

import top.lingkang.finalserver.server.FinalServerApplication;
import top.lingkang.finalserver.server.annotation.Controller;
import top.lingkang.finalserver.server.annotation.FinalServerBoot;
import top.lingkang.finalserver.server.annotation.GET;
import top.lingkang.finalserver.server.web.http.FinalServerContext;

import java.util.Date;

/**
 * @author lingkang
 * 2023/2/6
 * 测试数据库存储会话
 **/
@Controller
@FinalServerBoot
public class WebApp {
    public static void main(String[] args) {
        FinalServerApplication.run(WebApp.class, args);
    }

    @GET
    public Object index(FinalServerContext context) {
        context.getRequest().getSession().setAttribute("hi", new Date());
        return "index";
    }

    @GET("/s")
    public Object s(FinalServerContext context) {
        return context.getRequest().getSession();
    }
}
