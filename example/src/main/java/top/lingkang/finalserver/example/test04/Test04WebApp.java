package top.lingkang.finalserver.example.test04;

import top.lingkang.finalserver.server.FinalServerApplication;
import top.lingkang.finalserver.server.annotation.Controller;
import top.lingkang.finalserver.server.annotation.FinalServerBoot;
import top.lingkang.finalserver.server.annotation.GET;

/**
 * @author lingkang
 * Created by 2023/1/14
 * 测试对象入参，将入参赋值到对象中
 */
@FinalServerBoot
@Controller
public class Test04WebApp {
    public static void main(String[] args) {
        FinalServerApplication.run(Test04WebApp.class, args);
    }

    @GET
    public Object index(ParamInfo info) {
        System.out.println(info);
        if (info != null)
            System.out.println(info.getName() == null);
        return "ok";
    }
}
