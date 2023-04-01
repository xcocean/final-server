package top.lingkang.finalserver.example.test14;

import top.lingkang.finalserver.server.FinalServerApplication;
import top.lingkang.finalserver.server.annotation.*;
import top.lingkang.finalserver.server.web.http.FinalServerContext;
import top.lingkang.finalserver.server.web.http.MultipartFile;

import java.io.File;
import java.util.List;

/**
 * @author lingkang
 * Created by 2023/1/19
 */
@RequestMapping("/admin")
@Controller
@FinalServerBoot
public class WebApp {
    public static void main(String[] args) {
        FinalServerApplication.run(WebApp.class, args);
    }

    @GET
    public void index(FinalServerContext context) {
        context.getResponse().returnString("admin");
    }

}
