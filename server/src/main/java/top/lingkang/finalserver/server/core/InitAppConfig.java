package top.lingkang.finalserver.server.core;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;
import top.lingkang.finalserver.server.error.FinalServerException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
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

    public static void initProperties(String[] args, int port) throws Exception {
        InputStream banner = InitAppConfig.class.getClassLoader().getResourceAsStream("banner.txt");
        if (banner != null) {
            System.out.println();
            System.out.println(IoUtil.read(banner, StandardCharsets.UTF_8));
            System.out.println();
            IoUtil.close(banner);
        }
        try {
            Properties app = new Properties();
            app.load(InitAppConfig.class.getClassLoader().getResourceAsStream("final-server-application.properties"));
            InputStream in = getCustomConfig(args);// 获取定义的配置文件
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

            for (String arg : args) {
                if (arg.contains("=")) {
                    String[] split = arg.split("=");
                    System.setProperty(split[0], split[1]);
                }
            }

            // load to
            FinalServerProperties.load();
        } catch (Exception e) {
            throw e;
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


    // [server.config=application.properties]
    private static InputStream getCustomConfig(String[] args) throws FileNotFoundException {
        if (args.length == 0)
            return null;

        for (String item : args) {
            if (item.contains("server.config=")) {
                String pro = item.split("=")[1];
                File file = new File(pro);
                if (file.exists()) {
                    log.debug(file.getAbsolutePath());
                    return new FileInputStream(file);
                }

                InputStream resourceAsStream = InitAppConfig.class.getClassLoader().getResourceAsStream(pro);
                if (resourceAsStream == null)
                    throw new FinalServerException("未找到配置文件：" + pro + "    启动参数：" + item);
                else
                    return resourceAsStream;
            }
        }

        return InitAppConfig.class.getClassLoader().getResourceAsStream("application.properties");
    }

}
