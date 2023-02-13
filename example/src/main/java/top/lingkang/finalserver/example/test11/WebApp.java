package top.lingkang.finalserver.example.test11;

import top.lingkang.finalserver.server.FinalServerApplication;
import top.lingkang.finalserver.server.annotation.Controller;
import top.lingkang.finalserver.server.annotation.FinalServerBoot;
import top.lingkang.finalserver.server.annotation.GET;
import top.lingkang.finalserver.server.web.http.FinalServerContext;

/**
 * @author lingkang
 * 2023/2/13 测试beetl模板引擎
 **/
@Controller
@FinalServerBoot
public class WebApp {
    public static void main(String[] args) {
        FinalServerApplication.run(WebApp.class,args);
    }

    @GET
    public void index(FinalServerContext context){
        context.getResponse().returnTemplate("index-beetl.html");
    }
}
