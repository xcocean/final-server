package top.lingkang.finalserver.example.test13;

import top.lingkang.finalserver.server.FinalServerApplication;
import top.lingkang.finalserver.server.annotation.Controller;
import top.lingkang.finalserver.server.annotation.FinalServerBoot;
import top.lingkang.finalserver.server.annotation.GET;
import top.lingkang.finalserver.server.annotation.POST;
import top.lingkang.finalserver.server.web.http.FinalServerContext;
import top.lingkang.finalserver.server.web.http.MultipartFile;

import java.io.File;
import java.util.List;

/**
 * @author lingkang
 * Created by 2023/1/19
 */
@Controller
@FinalServerBoot
public class WebApp {
    public static void main(String[] args) {
        FinalServerApplication.run(WebApp.class, args);
    }

    @GET
    public void uploadFile(FinalServerContext context) {
        context.getResponse().returnTemplate("upload-file");
    }

    @POST("/up")
    public void up(FinalServerContext context) throws Exception {
        List<MultipartFile> fileUpload = context.getRequest().getFileUpload();
        if (fileUpload.isEmpty()) {
            System.out.println("上传文件为空！");
            return;
        }
        MultipartFile multipartFile = fileUpload.get(0);
        File file = new File("C:\\Users\\Administrator\\Desktop\\" + multipartFile.getFileName());

        multipartFile.transferTo(file);
        System.out.println("上传文件成功：" + multipartFile.getFileName());
    }
}
