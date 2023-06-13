package top.lingkang.finalserver.server.web;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import top.lingkang.finalserver.server.config.FinalServerProperties;
import top.lingkang.finalserver.server.core.*;
import top.lingkang.finalserver.server.core.impl.DefaultHttpSessionManage;
import top.lingkang.finalserver.server.core.impl.DefaultIdGenerateFactory;
import top.lingkang.finalserver.server.core.impl.DefaultWebExceptionHandler;
import top.lingkang.finalserver.server.utils.BeanUtils;
import top.lingkang.finalserver.server.web.handler.ControllerRequestHandler;
import top.lingkang.finalserver.server.web.handler.LocalStaticMapping;
import top.lingkang.finalserver.server.web.handler.RequestHandler;
import top.lingkang.finalserver.server.web.handler.StaticRequestHandler;
import top.lingkang.finalserver.server.web.http.Filter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author lingkang
 * 2022/12/15
 * @since 1.0.0
 **/
public class FinalServerInitializer {
    private static final Logger log = LoggerFactory.getLogger(FinalServerInitializer.class);
    public static RequestHandler[] requestHandlers = new RequestHandler[0];
    public static Filter[] filters = new Filter[0];

    @Autowired
    public FinalServerInitializer(ApplicationContext applicationContext) {
        settingConfig(applicationContext.getBean(FinalServerProperties.class));
        List<RequestHandler> handlers = new ArrayList<>();
        String[] beanNamesForType = applicationContext.getBeanNamesForType(LocalStaticMapping.class);
        for (String name : beanNamesForType) {
            LocalStaticMapping bean = applicationContext.getBean(name, LocalStaticMapping.class);
            bean.init();
            for (String path : bean.getPaths()) {
                handlers.add(bean);
                log.info("本地静态文件映射：" + path);
            }
        }

        // 注入
        handlers.add(applicationContext.getBean(StaticRequestHandler.class));// 项目静态文件

        ControllerRequestHandler controllerBean = applicationContext.getBean(ControllerRequestHandler.class);
        controllerBean.build();// 构建
        handlers.add(controllerBean);

        requestHandlers = handlers.toArray(new RequestHandler[0]);

        String[] namesForType = applicationContext.getBeanNamesForType(Filter.class);
        if (namesForType.length > 0) {
            List<Filter> list = new ArrayList<>();
            for (String name : namesForType) {
                Filter filter = applicationContext.getBean(name, Filter.class);
                filter.init();// 初始化
                list.add(filter);
            }

            // 排序
            list.sort(new Comparator<Filter>() {
                @Override
                public int compare(Filter o1, Filter o2) {
                    Class<? extends Filter> aClass = o1.getClass();
                    int v1 = 0, v2 = 0;
                    Order order = BeanUtils.getSpringProxyToClass(o1.getClass()).getAnnotation(Order.class);
                    if (order != null)
                        v1 = order.value();
                    order = BeanUtils.getSpringProxyToClass(o2.getClass()).getAnnotation(Order.class);
                    if (order != null)
                        v2 = order.value();

                    if (v1 == v2)
                        return 0;

                    return v1 > v2 ? 1 : -1;
                }
            });

            filters = list.toArray(new Filter[]{});
        }

        // 初始化异常处理
        WebExceptionHandler exceptionHandler = BeanUtils.getBean(WebExceptionHandler.class, applicationContext);
        if (exceptionHandler != null)
            FinalServerConfiguration.webExceptionHandler = exceptionHandler;
        else
            FinalServerConfiguration.webExceptionHandler = new DefaultWebExceptionHandler();

        // 初始化模板解析
        HttpParseTemplate httpParseTemplate = BeanUtils.getBean(HttpParseTemplate.class, applicationContext);
        if (httpParseTemplate != null)// 使用默认模板解析
            FinalServerConfiguration.httpParseTemplate = httpParseTemplate;
        FinalServerConfiguration.httpParseTemplate.init(FinalServerConfiguration.templatePath);
        log.info("模板引擎扫描路径：{}  后缀匹配：{}", FinalServerConfiguration.templatePath, FinalServerConfiguration.templateSuffix);

        // 初始化会话管理
        String[] sessionManage = applicationContext.getBeanNamesForType(HttpSessionManage.class);
        if (sessionManage.length > 0) {
            FinalServerConfiguration.httpSessionManage = applicationContext.getBean(sessionManage[0], HttpSessionManage.class);
            if (sessionManage.length > 1)
                log.warn("存在多个会话管理，应用了首个：{}", sessionManage[0]);
        } else
            FinalServerConfiguration.httpSessionManage = new DefaultHttpSessionManage();

        // id生成
        String[] generateId = applicationContext.getBeanNamesForType(IdGenerateFactory.class);
        if (generateId.length > 0) {
            FinalServerConfiguration.idGenerateFactory = applicationContext.getBean(generateId[0], IdGenerateFactory.class);
            if (generateId.length > 1)
                log.warn("存在多个Id生成器，应用了首个：{}", generateId[0]);
        } else
            FinalServerConfiguration.idGenerateFactory = new DefaultIdGenerateFactory();

        // web监听初始化
        String[] webListeners = applicationContext.getBeanNamesForType(WebListener.class);
        for (String listener : webListeners) {
            FinalServerConfiguration.webListener.add(applicationContext.getBean(listener, WebListener.class));
        }

        // 序列化初始化
        String[] serializable = applicationContext.getBeanNamesForType(SerializableObject.class);
        if (serializable.length > 0) {
            FinalServerConfiguration.serializable = applicationContext.getBean(serializable[0], SerializableObject.class);
        }

        // 返回文件处理
        ReturnStaticFileHandler staticFileHandler = BeanUtils.getBean(ReturnStaticFileHandler.class, applicationContext);
        if (staticFileHandler != null)
            FinalServerConfiguration.returnStaticFileHandler = staticFileHandler;

        // 默认响应头
        ServerDefaultHttpHeaders defaultHttpHeaders = BeanUtils.getBean(ServerDefaultHttpHeaders.class, applicationContext);
        if (defaultHttpHeaders != null)
            FinalServerConfiguration.defaultResponseHeaders = defaultHttpHeaders;

    }

    private void settingConfig(FinalServerProperties serverProperties) {
        if (!NetUtil.isUsableLocalPort(serverProperties.getPort()))
            throw new RuntimeException("FinalServer start fail  启动失败，端口被占用: " + serverProperties.getPort());

        if (serverProperties.getUploadFileBuffer() == -1 || serverProperties.getUploadFileBuffer() == 0)
            serverProperties.setUploadFileBuffer(Integer.MAX_VALUE);

        if (!serverProperties.getTemplatePath().endsWith("/"))
            serverProperties.setTemplatePath(serverProperties.getTemplatePath() + "/");
        if (!serverProperties.getTemplatePath().startsWith("/"))
            serverProperties.setTemplatePath("/" + serverProperties.getTemplatePath());

        String staticPath = serverProperties.getTemplateStatic();
        if (StrUtil.isBlank(staticPath)) {
            staticPath = "static";
        } else if (staticPath.startsWith("/"))
            staticPath = staticPath.substring(1);
        else if (staticPath.endsWith("/"))
            staticPath = staticPath.substring(0, staticPath.length() - 1);
        serverProperties.setTemplateStatic(staticPath);

        FinalServerConfiguration.templateStatic = serverProperties.getTemplateStatic();
        FinalServerConfiguration.templatePath = serverProperties.getTemplatePath();
        FinalServerConfiguration.templateSuffix = serverProperties.getTemplateSuffix();
        FinalServerConfiguration.templateCache = serverProperties.isTemplateCache();
        FinalServerConfiguration.templateCacheTime = serverProperties.getTemplateCacheTime();
        FinalServerConfiguration.sessionName = serverProperties.getSessionName();
        FinalServerConfiguration.sessionExpire = serverProperties.getSessionExpire();
        FinalServerConfiguration.uploadFileBuffer = serverProperties.getUploadFileBuffer();
        FinalServerConfiguration.maxContentLength = serverProperties.getMaxContentLength();
        FinalServerConfiguration.cacheControl = serverProperties.isCacheControl();

        int pro = Runtime.getRuntime().availableProcessors();
        int boss = pro, work = pro * 50;
        if (work > 200)
            work = 200;// 默认值不超过200
        if (serverProperties.getThreadMaxReceive() != 0)
            boss = serverProperties.getThreadMaxReceive();
        if (serverProperties.getThreadMaxHandler() != 0)
            work = serverProperties.getThreadMaxHandler();
        FinalServerConfiguration.threadMaxReceive = boss;
        FinalServerConfiguration.threadMaxHandler = work;
        FinalServerConfiguration.threadBacklog = serverProperties.getThreadBacklog();
        serverProperties.setThreadMaxReceive(boss);
        serverProperties.setThreadMaxHandler(work);
        Assert.isTrue(serverProperties.getThreadMaxReceive() > 0, "接收线程数不能小于1");
        Assert.isTrue(serverProperties.getThreadMaxHandler() > 0, "线程处理数不能小于1");

        FinalServerConfiguration.websocketMaxMessage = serverProperties.getWebsocketMaxMessage();
        FinalServerConfiguration.websocketTimeout = serverProperties.getWebsocketTimeout();

        FinalServerConfiguration.port=serverProperties.getPort();
        log.info("配置如下：{}", serverProperties.toString());
    }

}
