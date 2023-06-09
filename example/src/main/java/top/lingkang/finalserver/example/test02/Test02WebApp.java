package top.lingkang.finalserver.example.test02;

import io.netty.channel.ChannelHandlerContext;
import top.lingkang.finalserver.server.FinalServerApplication;
import top.lingkang.finalserver.server.annotation.Controller;
import top.lingkang.finalserver.server.annotation.FinalServerBoot;
import top.lingkang.finalserver.server.annotation.GET;
import top.lingkang.finalserver.server.core.FinalServerConfiguration;
import top.lingkang.finalserver.server.core.impl.DefaultWebExceptionHandler;
import top.lingkang.finalserver.server.web.http.FinalServerContext;
import top.lingkang.finalserver.server.web.http.HttpUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lingkang
 * 2023/1/12
 **/
@Controller
@FinalServerBoot
public class Test02WebApp {
    public static void main(String[] args) {
        FinalServerApplication.run(Test02WebApp.class, args);
    }

    @GET("")
    public String index() throws Exception {
        FinalServerConfiguration.webExceptionHandler = new DefaultWebExceptionHandler() {
            @Override
            public void exception(ChannelHandlerContext context, Throwable cause) throws Exception {
                // super.exception(context, cause);
                System.out.println("出现异常");
                // HttpUtils.sendTemplate(context,"index",500);
                // HttpUtils.sendEmpty(context,200);
                Map map = new HashMap();
                map.put("hello", "hello");
                HttpUtils.sendJSONObject(context, map, 200);
            }
        };
        return "ok";
    }

    @GET("/e")
    public String e() throws Exception {
        if (1 == 1)
            throw new RuntimeException("6666666");
        return "ok";
    }

    @GET("/r")
    public String r() throws Exception {
        FinalServerContext.currentContext().getResponse().returnRedirect("/e");
        return "ok";
    }
}
