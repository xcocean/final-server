package top.lingkang.finalserver.server.core.impl;

import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.ClasspathResourceLoader;
import top.lingkang.finalserver.server.core.HttpParseTemplate;
import top.lingkang.finalserver.server.web.http.FinalServerContext;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author lingkang
 * 2023/2/13
 **/
public class BeetlHttpParseTemplate implements HttpParseTemplate {
    public static Configuration configuration = null;
    public static GroupTemplate groupTemplate = null;

    @Override
    public void init(String templatePath) {
        ClasspathResourceLoader resourceLoader = new ClasspathResourceLoader(BeetlHttpParseTemplate.class.getClassLoader(), templatePath);
        try {
            configuration = Configuration.defaultConfiguration();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        groupTemplate = new GroupTemplate(resourceLoader, configuration);
    }

    @Override
    public byte[] getTemplate(String template, Map<String, Object> map, Map<String, Object> globalMap, FinalServerContext context) throws Exception {
        map.putAll(globalMap);
        Template t = groupTemplate.getTemplate(template);
        t.fastBinding(map);
        String render = t.render();
        return render.getBytes(StandardCharsets.UTF_8);
    }
}
