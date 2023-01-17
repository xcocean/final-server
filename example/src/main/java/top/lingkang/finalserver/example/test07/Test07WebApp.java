package top.lingkang.finalserver.example.test07;

import top.lingkang.finalserver.server.FinalServerApplication;
import top.lingkang.finalserver.server.annotation.Controller;
import top.lingkang.finalserver.server.annotation.FinalServerBoot;
import top.lingkang.finalserver.server.annotation.GET;
import top.lingkang.finalserver.server.web.http.FinalServerContext;
import top.lingkang.finalserver.server.web.http.ViewTemplate;

/**
 * @author lingkang
 * 2023/1/17
 * 检查全局模板渲染变量
 **/
@Controller
@FinalServerBoot
public class Test07WebApp {
    public static void main(String[] args) {
        FinalServerApplication.run(Test07WebApp.class, args);
        FinalServerContext.getTemplateGlobalMap().put("vv","asdasdasdssssssssssssssss");
    }

    @GET()
    public Object index(){
        return new ViewTemplate("index");
    }
}