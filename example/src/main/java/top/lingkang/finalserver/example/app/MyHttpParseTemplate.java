package top.lingkang.finalserver.example.app;

import top.lingkang.finalserver.server.core.HttpParseTemplate;
import top.lingkang.finalserver.server.web.http.FinalServerContext;

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
    public byte[] getTemplate(String template, Map<String, Object> map, Map<String, Object> globalMap, FinalServerContext context) throws Exception {
        // 在此处进行模板渲染，返回输出结果
        return new byte[0];
    }
}
