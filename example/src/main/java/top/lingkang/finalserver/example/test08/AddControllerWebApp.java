package top.lingkang.finalserver.example.test08;

import top.lingkang.finalserver.server.FinalServerApplication;
import top.lingkang.finalserver.server.annotation.Controller;
import top.lingkang.finalserver.server.annotation.FinalServerBoot;
import top.lingkang.finalserver.server.annotation.POST;
import top.lingkang.finalserver.server.web.http.FinalServerContext;

/**
 * @author lingkang
 * Created by 2023/1/19
 */
@Controller
@FinalServerBoot
public class AddControllerWebApp {
    public static void main(String[] args) {
        FinalServerApplication.run(AddControllerWebApp.class,args);
    }

    @POST
    public Object index(FinalServerContext context){
        String body = context.getRequest().getBody();
        System.out.println(body);
        return "ok";
    }
}
