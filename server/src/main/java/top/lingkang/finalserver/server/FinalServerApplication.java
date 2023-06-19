package top.lingkang.finalserver.server;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import top.lingkang.finalserver.server.annotation.FinalServerBoot;
import top.lingkang.finalserver.server.core.CustomRequestHandler;
import top.lingkang.finalserver.server.core.DynamicAddController;
import top.lingkang.finalserver.server.log.FinalSystemOut;
import top.lingkang.finalserver.server.web.handler.ControllerRequestHandler;
import top.lingkang.finalserver.server.web.http.RequestMethod;

/**
 * @author lingkang
 * Created by 2022/12/6
 * @since 1.0.0
 * 启动入口主类
 */
public class FinalServerApplication extends DynamicAddController {
    public static ApplicationContext applicationContext;
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

        System.setProperty("server.port", String.valueOf(port));

        try {
            // 启动spring
            applicationContext = SpringApplication.run(mainClass, args);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }


    /**
     * 用于动态添加http处理
     */
    public static void addRequestHandler(String path, RequestMethod method, CustomRequestHandler customRequestHandler) throws Exception {
        ControllerRequestHandler requestHandler = applicationContext.getBean(ControllerRequestHandler.class);
        requestHandler.addRequestHandler(path, method, customRequestHandler);
    }
}
