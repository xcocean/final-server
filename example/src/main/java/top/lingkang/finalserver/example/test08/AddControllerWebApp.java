package top.lingkang.finalserver.example.test08;

import top.lingkang.finalserver.server.FinalServerApplication;
import top.lingkang.finalserver.server.annotation.Controller;
import top.lingkang.finalserver.server.annotation.FinalServerBoot;

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
}
