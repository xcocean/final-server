package top.lingkang.finalserver.example.test04;

import top.lingkang.finalserver.server.FinalServerApplication;
import top.lingkang.finalserver.server.annotation.*;
import top.lingkang.finalserver.server.web.http.FinalServerContext;

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

    @POST("/p")
    public Object p(String name, ParamInfo info) {
        System.out.println("name=" + name);
        System.out.println(info);
        return "post";
    }

    @PUT("/put")
    public Object put(String name, ParamInfo info) {
        System.out.println("name=" + name);
        System.out.println(info);
        return "post";
    }

    @DELETE("/d")
    public Object d(String name, ParamInfo info) {
        System.out.println("name=" + name);
        System.out.println(info);
        return "post";
    }

    @GET("/c")
    public Object c(FinalServerContext context) {
        ParamInfo paramToBean = context.getRequest().getParamToBean(ParamInfo.class);
        return paramToBean;
    }
}
