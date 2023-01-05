package top.lingkang.finalserver.server;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.net.NetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import top.lingkang.finalserver.server.annotation.FinalServerBoot;
import top.lingkang.finalserver.server.core.FinalServerProperties;
import top.lingkang.finalserver.server.core.InitAppConfig;
import top.lingkang.finalserver.server.core.ShutdownEvent;
import top.lingkang.finalserver.server.core.impl.ShutdownEventRemoveTempConfigFile;
import top.lingkang.finalserver.server.log.FinalServerLogConfig;
import top.lingkang.finalserver.server.log.FinalSystemOut;
import top.lingkang.finalserver.server.web.FinalServerWeb;
import top.lingkang.finalserver.server.web.entity.RequestInfo;
import top.lingkang.finalserver.server.web.handler.ControllerRequestHandler;
import top.lingkang.finalserver.server.web.handler.CustomRequestHandler;
import top.lingkang.finalserver.server.web.handler.RequestHandler;
import top.lingkang.finalserver.server.web.http.FilterChain;
import top.lingkang.finalserver.server.web.http.FinalServerContext;
import top.lingkang.finalserver.server.web.http.RequestMethod;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author lingkang
 * Created by 2022/12/6
 * @since 1.0.0
 * 启动入口主类
 */
public class FinalServerApplication {
    public static ApplicationContext applicationContext;
    private static final Logger log = LoggerFactory.getLogger(FinalServerApplication.class);

    public static FinalServerLogConfig finalServerLogConfig;

    public static long startTime=0L;
    public static Class<?> mainClass;

    public static void run(Class<?> mainClass, String[] args) {
        run(mainClass, 7070, args);
    }

    public static void run(Class<?> mainClass, int port, String[] args) {
        startTime=System.currentTimeMillis();
        FinalServerApplication.mainClass=mainClass;
        FinalServerBoot mainClassAnnotation = mainClass.getAnnotation(FinalServerBoot.class);
        if (mainClassAnnotation == null) {
            FinalSystemOut.error("启动类未添加@FinalServerBoot，未执行相关功能");
            return;
        }

        // 加载配置
        InitAppConfig.initProperties(args, port);

        // 初始化日志配置
        finalServerLogConfig = new FinalServerLogConfig();

        // 检查端口
        if (!NetUtil.isUsableLocalPort(FinalServerProperties.server_port)) {
            log.error("FinalServer start fail  启动失败，端口被占用: {}", FinalServerProperties.server_port);
            System.exit(0);
            return;
        }

        try {
            // 配置spring xml
            InitAppConfig.initXml(mainClass);
            log.debug("FinalServer 配置加载完成");

            // 添加钩子
            addShutdownHook(new ShutdownEventRemoveTempConfigFile());
            addShutdownHook();

            // 启动spring
            applicationContext = new FileSystemXmlApplicationContext(InitAppConfig.getXmlPage());
        } catch (Exception e) {
            log.error("FinalServer 启动失败: ", e);
            System.exit(0);
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

    /**
     * 用于动态添加http处理
     */
    public static void addRequestHandler(String path, RequestMethod method, CustomRequestHandler customRequestHandler) throws Exception {
        Assert.notBlank(path, "处理路径不能为空");
        Assert.notNull(method, "请求方法不能为空");
        Assert.notNull(customRequestHandler, "自定义处理不能为空");
        if (!path.startsWith("/"))
            path = "/" + path;

        FinalServerWeb serverWeb = applicationContext.getBean(FinalServerWeb.class);
        Field field = serverWeb.getClass().getDeclaredField("filterChain");
        field.setAccessible(true);
        FilterChain filterChain = (FilterChain) field.get(serverWeb);
        RequestHandler[] requestHandler = filterChain.getRequestHandler();
        for (RequestHandler handler : requestHandler) {
            if (handler instanceof ControllerRequestHandler) {
                ControllerRequestHandler controllerRequestHandler = (ControllerRequestHandler) handler;
                Field absolutePath = controllerRequestHandler.getClass().getDeclaredField("absolutePath");
                absolutePath.setAccessible(true);
                HashMap<String, RequestInfo> map = (HashMap<String, RequestInfo>) absolutePath.get(controllerRequestHandler);
                if (map.containsKey(method.name() + "_" + path))
                    throw new IllegalArgumentException("已经存在的处理方法：" + method.name() + "  " + path);

                RequestInfo info = new RequestInfo();
                info.setParamName(new String[]{"context"});
                info.setParamType(new Class[]{FinalServerContext.class});
                info.setMethodName(method.name());
                info.setReturnType(null);
                info.setCustomRequestHandler(customRequestHandler);
                info.setBeanName(null);
                map.put(method.name() + "_" + path, info);
                log.info("添加自定义请求处理成功：" + method.name() + "  " + path);
                break;
            }
        }
    }
}
