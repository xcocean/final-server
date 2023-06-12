package top.lingkang.finalserver.example.test01;

import lombok.extern.slf4j.Slf4j;
import top.lingkang.finalserver.server.FinalServerApplication;
import top.lingkang.finalserver.server.annotation.Controller;
import top.lingkang.finalserver.server.annotation.FinalServerBoot;
import top.lingkang.finalserver.server.annotation.GET;

import java.util.Timer;
import java.util.TimerTask;

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

    private int count=0;
    @GET("")
    public String index() throws Exception {
        // log.info("ok");
        count++;
        return "ok";
    }

    @GET("a")
    public String a() throws Exception {
        return "ok";
    }

    public Test01WebApp() {
        Timer timer = new Timer();timer.schedule(new TimerTask() {
            @Override
            public void run() {
                log.info("请求次数：{}",count);
            }
        },5000,5000);
    }
}
