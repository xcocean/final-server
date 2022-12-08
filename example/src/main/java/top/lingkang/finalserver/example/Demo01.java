package top.lingkang.finalserver.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
import top.lingkang.finalserver.server.FinalServerApplication;
import top.lingkang.finalserver.server.annotation.Controller;
import top.lingkang.finalserver.server.annotation.FinalServerBoot;
import top.lingkang.finalserver.server.annotation.GET;
import top.lingkang.finalserver.server.web.http.HttpResponse;
import top.lingkang.finalserver.server.web.http.Response;

/**
 * @author lingkang
 * Created by 2022/12/6
 */
@FinalServerBoot
@Controller
public class Demo01 {
    @Autowired
    private Environment environment;

    public static void main(String[] args) {
        FinalServerApplication.run(Demo01.class, args);
        StandardEnvironment bean = FinalServerApplication.applicationContext.getBean(StandardEnvironment.class);
        System.out.println(bean.getProperty("server.port"));
    }

    @GET
    public void index(HttpResponse response){
        System.out.println("123");
        response.returnString("hi你好啊");
    }
}
