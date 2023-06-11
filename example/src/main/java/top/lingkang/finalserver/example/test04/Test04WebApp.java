package top.lingkang.finalserver.example.test04;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
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
@EnableScheduling
public class Test04WebApp {
    public static void main(String[] args) {
        FinalServerApplication.run(Test04WebApp.class, args);
    }

    @CheckLogin
    @GET
    public Object index(ParamInfo info, String name) {
        System.out.println(info);
        if (info != null)
            System.out.println(info.getName() == null);
        System.out.println(name);
        return "ok";
    }

    @GET("/t")
    public Object t(String name, ParamInfo info) {
        System.out.println(name);
        System.out.println(info);
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


    @Autowired
    protected ApplicationContext applicationContext;

    // @Scheduled(fixedRate = 5000)
    public void task() {
        Test04WebApp bean = applicationContext.getBean(Test04WebApp.class);
    }

    public String hi(String name) {
        return "hi +" + name;
    }
}
