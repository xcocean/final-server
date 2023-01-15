package top.lingkang.finalserver.example.test05;

import top.lingkang.finalserver.server.FinalServerApplication;
import top.lingkang.finalserver.server.annotation.Controller;
import top.lingkang.finalserver.server.annotation.FinalServerBoot;
import top.lingkang.finalserver.server.annotation.GET;
import top.lingkang.finalserver.server.web.http.ViewTemplate;

/**
 * @author lingkang
 * Created by 2023/1/15
 * 主要用于测试 http、http响应头
 */
@FinalServerBoot
@Controller
public class Test05WebApp {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        FinalServerApplication.run(Test05WebApp.class, args);
        System.out.println(System.currentTimeMillis()-start);
    }

    @GET
    public Object index(){
        return new ViewTemplate("index");
    }

}
