package top.lingkang.finalserver.example.test.config;

import org.springframework.stereotype.Component;
import top.lingkang.finalserver.server.web.handler.LocalStaticMapping;

import java.util.List;

/**
 * @author lingkang
 * Created by 2022/12/9
 */
// @Component
public class MyLocalStaticMapping extends LocalStaticMapping {

    @Override
    public void addStaticByAbsolutePath(List<String> paths) {
        paths.add("d:/");
        paths.add("C:\\Users\\Administrator\\Desktop\\picture");
    }
}
