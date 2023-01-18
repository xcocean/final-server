package top.lingkang.finalserver.example.app;

import org.springframework.stereotype.Component;
import top.lingkang.finalserver.server.web.handler.LocalStaticMapping;

import java.util.List;

/**
 * @author lingkang
 * Created by 2023/1/18
 */
@Component
public class MyLocalStaticMapping extends LocalStaticMapping {
    @Override
    public void addStaticByAbsolutePath(List<String> paths) {
        paths.add("d:/");
        paths.add("C:\\Users\\Administrator\\Desktop\\picture");
    }

}