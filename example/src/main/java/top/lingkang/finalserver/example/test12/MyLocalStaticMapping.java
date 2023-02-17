package top.lingkang.finalserver.example.test12;

import org.springframework.stereotype.Component;
import top.lingkang.finalserver.server.web.handler.LocalStaticMapping;

import java.util.List;

/**
 * @author lingkang
 * 2023/2/17
 **/
@Component
public class MyLocalStaticMapping extends LocalStaticMapping {
    @Override
    public void addStaticByAbsolutePath(List<String> paths) {
        paths.add(System.getProperty("user.dir"));
    }
}
