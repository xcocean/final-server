package top.lingkang.finalserver.example.test12;

import top.lingkang.finalserver.server.FinalServerApplication;
import top.lingkang.finalserver.server.annotation.Controller;
import top.lingkang.finalserver.server.annotation.FinalServerBoot;

/**
 * @author lingkang
 * Created by 2023/1/19
 */
@Controller
@FinalServerBoot
public class WebApp {
    public static void main(String[] args) {
        FinalServerApplication.run(WebApp.class,args);
    }
}
