package top.lingkang.finalserver.server.web.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import top.lingkang.finalserver.server.web.entity.CacheRequestHandler;
import top.lingkang.finalserver.server.web.entity.RequestInfo;
import top.lingkang.finalserver.server.web.http.FinalServerContext;
import top.lingkang.finalserver.server.web.http.ViewTemplate;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lingkang
 * Created by 2022/12/7
 * @since 1.0.0
 */
public class ControllerRequestHandler implements RequestHandler {
    private static final Logger log = LoggerFactory.getLogger(ControllerRequestHandler.class);
    private HashMap<String, RequestInfo> absolutePath;
    private ApplicationContext applicationContext;
    private MethodHandlerParam handlerParam = new MethodHandlerParam();
    public static Map<String, CacheRequestHandler> cacheRequestHandler = new HashMap<>();

    public ControllerRequestHandler(HashMap<String, RequestInfo> absolutePath, ApplicationContext applicationContext) {
        this.absolutePath = absolutePath;
        this.applicationContext = applicationContext;
    }

    public boolean handler(FinalServerContext context) throws Exception {
        String reqURL = context.getRequest().getHttpMethod().name() + "_" + context.getRequest().getPath();
        RequestInfo requestInfo = absolutePath.get(reqURL);
        if (requestInfo != null) {
            if (requestInfo.getBeanName() == null) {// 自定义的请求处理
                requestInfo.getCustomRequestHandler().handler(context);
                return true;
            }

            // 缓存
            CacheRequestHandler handler = cacheRequestHandler.get(reqURL);
            if (handler == null) {
                // 没有时从上下文中获取
                Object bean = applicationContext.getBean(requestInfo.getBeanName());
                Method method = bean.getClass().getDeclaredMethod(requestInfo.getMethodName(), requestInfo.getParamType());
                handler = new CacheRequestHandler(bean, method);
                cacheRequestHandler.put(reqURL, handler);
            }

            Object[] param = joinParam(requestInfo, context);
            Object result = handler.getMethod().invoke(handler.getBean(), param);
            if (context.getResponse().isReady())
                return true;

            // 无模板，返回值时
            if (result == null) {
                // 返回空时，直接输出空字符串
                context.getResponse().returnString("");
            } else if (result instanceof ViewTemplate) {// 返回视图模板时
                ViewTemplate template = (ViewTemplate) result;
                if (context.getResponse().getTemplateMap() == null) {
                    context.getResponse().returnTemplate(template.getTemplate(), template.getMap());
                } else {
                    context.getResponse().getTemplateMap().putAll(template.getMap());
                    context.getResponse().returnTemplate(template.getTemplate());
                }
            } else {
                // 其他结果返回JSON格式化尝试
                context.getResponse().returnJsonObject(result);
            }
        }
        return true;
    }

    private Object[] joinParam(RequestInfo handler, FinalServerContext context) {
        if (handler.getParamType().length == 0)
            return handler.getParamType();
        Object[] params = new Object[handler.getParamType().length];
        for (int i = 0; i < handler.getParamType().length; i++) {
            params[i] = handlerParam.match(handler.getParamName()[i], handler.getParamType()[i], context);
        }
        return params;
    }
}
