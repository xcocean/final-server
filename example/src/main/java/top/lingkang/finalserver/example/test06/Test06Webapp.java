package top.lingkang.finalserver.example.test06;

import top.lingkang.finalserver.server.FinalServerApplication;
import top.lingkang.finalserver.server.annotation.*;

import java.util.Map;

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
    public Object index(String param) {
        System.out.println(param);
        return "index";
    }

    @GET("/{param}")
    public Object get3(String param) {
        System.out.println(3);
        System.out.println(param);
        return param;
    }

    @GET("/a/{id}")
    public Object get2(@RequestParam("id")String param, ParamInfo info) {
        System.out.println(param);
        System.out.println(info);
        return param;
    }

    @GET("/h/{a}/{b}")
    public Object h(String a, String b, @RequestHeader Map map){
        System.out.println(b);
        System.out.println(map);
        return a;
    }

}
