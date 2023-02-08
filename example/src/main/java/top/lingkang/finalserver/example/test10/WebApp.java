package top.lingkang.finalserver.example.test10;

import top.lingkang.finalserver.server.FinalServerApplication;
import top.lingkang.finalserver.server.annotation.Controller;
import top.lingkang.finalserver.server.annotation.FinalServerBoot;
import top.lingkang.finalserver.server.annotation.GET;
import top.lingkang.finalserver.server.web.http.FinalServerContext;
import top.lingkang.finalserver.server.web.security.annotation.EnableFinalSecurityAnnotation;
import top.lingkang.finalserver.server.web.security.annotation.FinalCheckLogin;

/**
 * @author lingkang
 * 2023/2/8
 **/
@EnableFinalSecurityAnnotation
@Controller
@FinalServerBoot
public class WebApp {
    public static void main(String[] args) {
        FinalServerApplication.run(WebApp.class, args);
    }

    @GET
    public void index(FinalServerContext context){
        context.getResponse().returnString("111");
    }

    @FinalCheckLogin
    @GET("l")
    public void l(FinalServerContext context){
        context.getResponse().returnString("111");
    }
}
