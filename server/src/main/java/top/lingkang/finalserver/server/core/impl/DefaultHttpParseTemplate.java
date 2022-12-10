package top.lingkang.finalserver.server.core.impl;

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import top.lingkang.finalserver.server.core.HttpParseTemplate;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author lingkang
 * Created by 2022/12/11
 */
public class DefaultHttpParseTemplate implements HttpParseTemplate {
    private static final Configuration config = new Configuration(Configuration.VERSION_2_3_27);

    @Override
    public void init(String templatePath) {
        config.setClassForTemplateLoading(DefaultHttpParseTemplate.class, templatePath);
        config.setTemplateLoader(new ClassTemplateLoader(DefaultHttpParseTemplate.class, templatePath));
        config.setDefaultEncoding("UTF-8");
        config.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        config.setLogTemplateExceptions(false);
        config.setWrapUncheckedExceptions(true);
    }

    @Override
    public byte[] getTemplate(String template, Map map) throws Exception {
        Template temp = config.getTemplate(template);
        StringWriter stringWriter = new StringWriter();
        temp.process(map, stringWriter);
        return stringWriter.toString().getBytes(StandardCharsets.UTF_8);
    }
}
