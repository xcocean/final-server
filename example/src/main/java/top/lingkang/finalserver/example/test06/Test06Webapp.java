package top.lingkang.finalserver.example.test06;

import top.lingkang.finalserver.server.FinalServerApplication;
import top.lingkang.finalserver.server.annotation.Controller;
import top.lingkang.finalserver.server.annotation.FinalServerBoot;
import top.lingkang.finalserver.server.annotation.GET;

/**
 * @author lingkang
 * 2023/1/16
 * 用于测试 rest ful 风格api
 **/
@Controller
@FinalServerBoot
public class Test06Webapp {
    public static void main(String[] args) {
        FinalServerApplication.run(Test06Webapp.class, args);
    }

    @GET()
    public Object index(){
        return "index";
    }

    @GET("/{param}")
    public Object get(String param){
        return param;
    }

    @GET("/a/{param}")
    public Object get2(String param){
        return param;
    }
}
