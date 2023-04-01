package top.lingkang.finalserver.server;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.IdUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import top.lingkang.finalserver.server.annotation.FinalServerBoot;
import top.lingkang.finalserver.server.core.CustomRequestHandler;
import top.lingkang.finalserver.server.core.DynamicAddController;
import top.lingkang.finalserver.server.core.FinalServerProperties;
import top.lingkang.finalserver.server.core.ShutdownEvent;
import top.lingkang.finalserver.server.error.FinalServerException;
import top.lingkang.finalserver.server.log.FinalServerLogConfig;
import top.lingkang.finalserver.server.log.FinalSystemOut;
import top.lingkang.finalserver.server.web.handler.ControllerRequestHandler;
import top.lingkang.finalserver.server.web.http.RequestMethod;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author lingkang
 * Created by 2022/12/6
 * @since 1.0.0
 * 启动入口主类
 */
public class FinalServerApplication extends DynamicAddController {
    public static ApplicationContext applicationContext;
    private static final Logger log = LoggerFactory.getLogger(FinalServerApplication.class);

    public static FinalServerLogConfig finalServerLogConfig;

    public static long startTime = 0L;
    public static Class<?> mainClass;

    public static void run(Class<?> mainClass, String[] args) {
        run(mainClass, 0, args);
    }

    public static void run(Class<?> mainClass, int port, String[] args) {
        startTime = System.currentTimeMillis();
        FinalServerApplication.mainClass = mainClass;
        FinalServerBoot mainClassAnnotation = mainClass.getAnnotation(FinalServerBoot.class);
        if (mainClassAnnotation == null) {
            FinalSystemOut.error("启动类未添加 @FinalServerBoot，未执行相关功能");
            return;
        }

        try {
            // 加载配置
            initProperties(mainClass, args, port);

            // 初始化日志配置
            finalServerLogConfig = new FinalServerLogConfig();

            // 检查端口
            if (!NetUtil.isUsableLocalPort(FinalServerProperties.server_port)) {
                throw new RuntimeException("FinalServer start fail  启动失败，端口被占用: " + FinalServerProperties.server_port);
            }

            // 配置spring xml
            initXml(mainClass);
            log.debug("FinalServer 配置加载完成");

            // 添加钩子
            addShutdownHook();

            // 启动spring
            applicationContext = new ClassPathXmlApplicationContext(getXmlPath());
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        } finally {
            if (xmlFile != null)
                xmlFile.delete();
        }
    }


    private static List<ShutdownEvent> shutdownEventList = new ArrayList<>();

    /**
     * 正常结束程序时执行，它是不可靠的。因为进程被杀死 kill -9 是不会执行下面的事件的
     */
    public static void addShutdownHook(ShutdownEvent shutdownEvent) {
        shutdownEventList.add(shutdownEvent);
    }

    private static void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            for (ShutdownEvent event : shutdownEventList) {
                try {
                    event.shutdown();
                } catch (Exception e) {
                    log.error("shutdown", e);
                }
            }
            log.info("FinalServer shutdown finish");
        }));
    }

    /**
     * 用于动态添加http处理
     */
    public static void addRequestHandler(String path, RequestMethod method, CustomRequestHandler customRequestHandler) throws Exception {
        ControllerRequestHandler requestHandler = applicationContext.getBean(ControllerRequestHandler.class);
        requestHandler.addRequestHandler(path, method, customRequestHandler);
    }


    public static void initProperties(Class<?> mainClass, String[] args, int port) throws Exception {
        InputStream banner = FinalServerApplication.class.getClassLoader().getResourceAsStream("banner.txt");
        if (banner != null) {
            System.out.println();
            System.out.println(IoUtil.read(banner, StandardCharsets.UTF_8));
            System.out.println();
            IoUtil.close(banner);
        }
        try {
            Properties app = new Properties();
            app.load(FinalServerApplication.class.getClassLoader().getResourceAsStream("final-server-application.properties"));
            InputStream in = getCustomConfig(mainClass, args);// 获取定义的配置文件
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

    // [server.config=application.properties]
    private static InputStream getCustomConfig(Class<?> mainClass, String[] args) throws FileNotFoundException {
        for (String item : args) {
            if (item.contains("server.config=")) {
                String pro = item.split("=")[1];
                File file = new File(pro);
                if (file.exists()) {
                    log.debug(file.getAbsolutePath());
                    return new FileInputStream(file);
                }

                InputStream resourceAsStream = mainClass.getClassLoader().getResourceAsStream(pro);
                if (resourceAsStream == null)
                    throw new FinalServerException("未找到配置文件：" + pro + "    启动参数：" + item);
                else
                    return resourceAsStream;
            }
        }

        // application.properties
        FinalServerBoot mainClassAnnotation = mainClass.getAnnotation(FinalServerBoot.class);
        return mainClass.getClassLoader().getResourceAsStream(mainClassAnnotation.value());
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

        String xml = IoUtil.readUtf8(FinalServerApplication.class.getClassLoader().getResourceAsStream("final-server-spring.xml"));
        xml = xml.replace("#componentScan", packageName);

        try {
            xmlFile = File.createTempFile("temp-final-server-spring-" + IdUtil.objectId(), ".xml");
            FileUtil.writeString(xml, xmlFile, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getXmlPath() {
        log.debug(xmlFile.getAbsolutePath());
        if (xmlFile.getAbsolutePath().startsWith("/")) {
            return "file:" + xmlFile.getAbsolutePath();
        }
        return "file:/" + xmlFile.getAbsolutePath();
    }
}
