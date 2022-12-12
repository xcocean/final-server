package top.lingkang.finalserver.server.core;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;
import top.lingkang.finalserver.server.FinalServerApplication;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;

/**
 * @author lingkang
 * Created by 2022/12/6
 * @since 1.0.0
 */
public class InitAppConfig {
    private static final Logger log = LoggerFactory.getLogger(InitAppConfig.class);

    public static void initProperties(String[] args, int port) {
        InputStream banner = InitAppConfig.class.getClassLoader().getResourceAsStream("banner.txt");
        if (banner != null) {
            System.out.println();
            System.out.println(IoUtil.read(banner, Charset.forName("UTF-8")));
            System.out.println();
            IoUtil.close(banner);
        }
        try {
            Properties app = new Properties();
            app.load(InitAppConfig.class.getClassLoader().getResourceAsStream("final-server-application.properties"));
            InputStream in = InitAppConfig.class.getClassLoader().getResourceAsStream("application.properties");
            if (in != null) {
                app.load(in);
            }
            if (port != 0)
                app.setProperty("server.port", port + "");
            for (Map.Entry<Object, Object> entry : app.entrySet()) {
                // 环境已经存在的值，不应该将它覆盖
                if (System.getProperties().getProperty(entry.getKey().toString()) != null)
                    continue;
                System.setProperty(entry.getKey().toString(), entry.getValue().toString());
            }
            if (app.getProperty("debug") != null && app.getProperty("debug").equals("true")) {
                FinalServerApplication.finalServerLogConfig.setLogLevel("DEBUG");
            }

            // load to
            FinalServerProperties.load();
        } catch (Exception e) {
            log.error("初始化应用配置异常：", e);
        }
    }

    private static File xmlFile;

    public static void initXml(Class<?> mainClass) {
        String packageName = mainClass.getPackage().getName();
        ComponentScan componentScan = mainClass.getAnnotation(ComponentScan.class);
        if (componentScan != null && componentScan.value().length > 0) {
            for (String pack : componentScan.value()) {
                packageName += ",";
                packageName += pack;

            }
            if (packageName.endsWith(","))
                packageName = packageName.substring(0, packageName.length() - 1);
        }

        String xml = IoUtil.readUtf8(InitAppConfig.class.getClassLoader().getResourceAsStream("final-server-spring.xml"));
        xml = xml.replace("#componentScan", packageName);

        try {
            xmlFile = File.createTempFile("final-server-spring-" + IdUtil.objectId(), ".xml");
            FileUtil.writeString(xml, xmlFile, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getXmlPage() {
        log.debug(xmlFile.getAbsolutePath());
        return xmlFile.getPath();
    }

}
