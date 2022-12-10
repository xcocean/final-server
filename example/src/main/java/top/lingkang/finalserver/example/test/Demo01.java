package top.lingkang.finalserver.example.test;

import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import top.lingkang.finalserver.server.FinalServerApplication;
import top.lingkang.finalserver.server.annotation.Controller;
import top.lingkang.finalserver.server.annotation.FinalServerBoot;
import top.lingkang.finalserver.server.annotation.GET;
import top.lingkang.finalserver.server.web.http.FinalServerContext;
import top.lingkang.finalserver.server.web.http.HttpResponse;
import top.lingkang.finalserver.server.web.http.RequestMethod;
import top.lingkang.finalserver.server.web.FinalServerHttpContext;

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

    @GET
    public void index(HttpResponse response, String name, int a) {
        System.out.println(name);
        System.out.println(a);
        // response.returnString("hi你好啊");
        System.out.println(FinalServerHttpContext.getRequest().requestId());
        response.returnTemplate("index.html");
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
        Set<Cookie> cookies = context.getRequest().getCookies();
        for (Cookie cookie : cookies)
            System.out.println(cookie.name() + "   " + cookie.value());
        // 添加cookie
        DefaultCookie lk = new DefaultCookie("lk", System.currentTimeMillis() + "");
        lk.setMaxAge(666);
        lk.setPath("/");
        context.getResponse().addCookie(lk);
    }
}
