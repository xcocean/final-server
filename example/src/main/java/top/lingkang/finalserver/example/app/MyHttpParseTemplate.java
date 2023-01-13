package top.lingkang.finalserver.example.app;

import org.springframework.stereotype.Component;
import top.lingkang.finalserver.server.core.HttpParseTemplate;

import java.util.Map;

/**
 * @author lingkang
 * Created by 2023/1/8
 * 自定义模板渲染
 */
// @Component
public class MyHttpParseTemplate implements HttpParseTemplate {
    @Override
    public void init(String templatePath) {

    }

    @Override
    public byte[] getTemplate(String template, Map map) throws Exception {
        // 在此处进行模板渲染，返回输出结果
        return null;
    }
}
