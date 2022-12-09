package top.lingkang.finalserver.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import top.lingkang.finalserver.server.annotation.FinalServerBoot;
import top.lingkang.finalserver.server.core.InitAppConfig;
import top.lingkang.finalserver.server.core.ShutdownEvent;
import top.lingkang.finalserver.server.core.impl.ShutdownEventRemoveTempConfigFile;
import top.lingkang.finalserver.server.log.FinalServerLogConfig;
import top.lingkang.finalserver.server.log.FinalSystemOut;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lingkang
 * Created by 2022/12/6
 * @since 1.0.0
 */
public class FinalServerApplication {
    public static ApplicationContext applicationContext;
    private static final Logger log = LoggerFactory.getLogger(FinalServerApplication.class);

    public static FinalServerLogConfig finalServerLogConfig;

    public static void run(Class<?> mainClass, String[] args) {
        run(mainClass, 7070, args);
    }

    public static void run(Class<?> mainClass, int port, String[] args) {
        FinalServerBoot mainClassAnnotation = mainClass.getAnnotation(FinalServerBoot.class);
        if (mainClassAnnotation == null) {
            FinalSystemOut.error("启动类未添加@FinalServerBoot，未执行相关功能");
            return;
        }
        // 初始化日志配置
        finalServerLogConfig = new FinalServerLogConfig();

        log.info("FinalServer 开始加载配置");
        InitAppConfig.initProperties(args);
        try {
            InitAppConfig.initXml(mainClass);
            addShutdownHook(new ShutdownEventRemoveTempConfigFile());
            addShutdownHook();
            applicationContext = new FileSystemXmlApplicationContext(InitAppConfig.getXmlPage());
        } catch (Exception e) {
            log.error("FinalServer 启动失败: ", e);
        }
    }


    private static List<ShutdownEvent> shutdownEventList = new ArrayList<>();

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
}
