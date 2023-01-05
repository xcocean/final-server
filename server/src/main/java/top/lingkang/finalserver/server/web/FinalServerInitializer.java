package top.lingkang.finalserver.server.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import top.lingkang.finalserver.server.FinalServerApplication;
import top.lingkang.finalserver.server.core.*;
import top.lingkang.finalserver.server.core.impl.DefaultHttpParseTemplate;
import top.lingkang.finalserver.server.core.impl.DefaultHttpSessionManage;
import top.lingkang.finalserver.server.core.impl.DefaultIdGenerateFactory;
import top.lingkang.finalserver.server.core.impl.DefaultWebExceptionHandler;
import top.lingkang.finalserver.server.utils.BeanUtils;
import top.lingkang.finalserver.server.web.handler.*;
import top.lingkang.finalserver.server.web.http.Filter;
import top.lingkang.finalserver.server.web.nio.ws.WebSocketManage;

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
    private final ApplicationContext applicationContext;
    public static RequestHandler[] requestHandlers = new RequestHandler[0];
    public static Filter[] filters = new Filter[0];
    public static HttpParseTemplate httpParseTemplate;

    @Autowired
    public FinalServerInitializer(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        List<RequestHandler> handlers = new ArrayList<>();
        String[] beanNamesForType = applicationContext.getBeanNamesForType(LocalStaticMapping.class);
        if (beanNamesForType.length > 0) {
            for (String name : beanNamesForType) {

                LocalStaticMapping bean = (LocalStaticMapping) applicationContext.getBean(name);
                for (String path : bean.getPaths()) {
                    handlers.add(new LocalStaticRequestHandler(path));
                    log.info("本地静态文件映射：" + path);
                }
            }
        }
        handlers.add(new StaticRequestHandler());// 项目静态文件
        handlers.add(new BuildControllerHandler(applicationContext).build());// controller转发
        requestHandlers = handlers.toArray(new RequestHandler[0]);

        String[] namesForType = applicationContext.getBeanNamesForType(Filter.class);
        if (namesForType.length > 0) {
            List<Filter> list = new ArrayList<>();
            for (String name : namesForType) {
                Filter filter = (Filter) applicationContext.getBean(name);
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

            // 添加注销事件
            FinalServerApplication.addShutdownHook(new ShutdownEvent() {
                @Override
                public void shutdown() throws Exception {
                    for (Filter filter : list)
                        filter.destroy();
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
        httpParseTemplate = BeanUtils.getBean(HttpParseTemplate.class, applicationContext);
        if (httpParseTemplate == null)// 使用默认模板解析
            httpParseTemplate = BeanUtils.getBean(DefaultHttpParseTemplate.class, applicationContext);
        httpParseTemplate.init(FinalServerProperties.server_template);

        // 初始化会话管理
        String[] sessionManage = applicationContext.getBeanNamesForType(HttpSessionManage.class);
        if (sessionManage.length > 0) {
            FinalServerConfiguration.httpSessionManage = applicationContext.getBean(sessionManage[0], HttpSessionManage.class);
            if (sessionManage.length > 1)
                log.warn("存在多个会话管理，应用了首个：{}", sessionManage[0]);
            log.info("use redis store session.");
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
    }

    @Order(Integer.MAX_VALUE)// 最后加载
    @Bean
    public WebSocketManage websocketManage() {
        return new WebSocketManage(applicationContext);
    }
}
