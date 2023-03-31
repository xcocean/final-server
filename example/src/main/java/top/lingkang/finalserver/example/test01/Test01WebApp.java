package top.lingkang.finalserver.example.test01;

import lombok.extern.slf4j.Slf4j;
import top.lingkang.finalserver.server.FinalServerApplication;
import top.lingkang.finalserver.server.annotation.Controller;
import top.lingkang.finalserver.server.annotation.FinalServerBoot;
import top.lingkang.finalserver.server.annotation.GET;

/**
 * @author lingkang
 * 2023/1/12
 **/
@Slf4j
@Controller
@FinalServerBoot
public class Test01WebApp {
    public static void main(String[] args) {
        FinalServerApplication.run(Test01WebApp.class, args);
    }

    @GET("")
    public String index() throws Exception {
        log.info("ok");
        return "ok";
    }
}
