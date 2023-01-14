package top.lingkang.finalserver.example.test03;

import org.springframework.stereotype.Controller;
import top.lingkang.finalserver.server.FinalServerApplication;
import top.lingkang.finalserver.server.annotation.FinalServerBoot;
import top.lingkang.finalserver.server.annotation.GET;
import top.lingkang.finalserver.server.web.http.FinalServerContext;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lingkang
 * 2023/1/13
 **/
@Controller
@FinalServerBoot
public class Test03WebApp {
    public static void main(String[] args) {
        FinalServerApplication.run(Test03WebApp.class, args);
    }

    @GET("")
    public Object index(String a) {
        System.out.println(a);
        Map map = new HashMap();
        map.put("hello", "asdasdsadsaas");
        // return map;
        MyUser myUser = new MyUser();
        myUser.setUsername("asd");
        return myUser;
    }

    @GET("/f")
    public void f(FinalServerContext context) {
        // 转发
        context.getResponse().returnForward("/f");
    }
}
