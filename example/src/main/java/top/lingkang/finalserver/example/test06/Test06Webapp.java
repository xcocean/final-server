package top.lingkang.finalserver.example.test06;

import top.lingkang.finalserver.server.FinalServerApplication;
import top.lingkang.finalserver.server.annotation.Controller;
import top.lingkang.finalserver.server.annotation.FinalServerBoot;
import top.lingkang.finalserver.server.annotation.GET;
import top.lingkang.finalserver.server.annotation.RequestParam;

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
    public Object get(String param) {
        System.out.println(param);
        return param;
    }

    @GET("/{param}")
    public Object get3(String param) {
        System.out.println(3);
        System.out.println(param);
        return param;
    }

    @GET("/a/{aa}")
    public Object get2(@RequestParam("aa")String param, ParamInfo info) {
        System.out.println(param);
        System.out.println(info);
        return param;
    }

}
