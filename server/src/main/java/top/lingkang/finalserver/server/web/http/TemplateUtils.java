package top.lingkang.finalserver.server.web.http;

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author lingkang
 * Created by 2022/12/8
 * @since 1.0.0
 */
public class TemplateUtils {
    private static final Configuration config = new Configuration(Configuration.VERSION_2_3_27);

    @Autowired
    private Environment environment;

    private void init() {
        String path = environment.getProperty("server.template", "/template");
        config.setClassForTemplateLoading(TemplateUtils.class, path);
        config.setTemplateLoader(new ClassTemplateLoader(TemplateUtils.class, path));
        config.setDefaultEncoding("UTF-8");
        config.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        config.setLogTemplateExceptions(false);
        config.setWrapUncheckedExceptions(true);
    }

    public static byte[] getTemplate(String template, Map map) throws Exception {
        Template temp = config.getTemplate(template);
        StringWriter stringWriter = new StringWriter();
        temp.process(map, stringWriter);
        return stringWriter.toString().getBytes(StandardCharsets.UTF_8);
    }
}
