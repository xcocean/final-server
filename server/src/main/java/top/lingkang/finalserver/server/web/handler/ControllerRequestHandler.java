package top.lingkang.finalserver.server.web.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import top.lingkang.finalserver.server.utils.MatchUtils;
import top.lingkang.finalserver.server.utils.TypeUtils;
import top.lingkang.finalserver.server.web.entity.CacheRequestHandler;
import top.lingkang.finalserver.server.web.entity.RequestInfo;
import top.lingkang.finalserver.server.web.http.FinalServerContext;
import top.lingkang.finalserver.server.web.http.ViewTemplate;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lingkang
 * Created by 2022/12/7
 * @since 1.0.0
 */
public class ControllerRequestHandler extends BuildControllerHandler implements RequestHandler {
    private static final Logger log = LoggerFactory.getLogger(ControllerRequestHandler.class);
    /*private HashMap<String, RequestInfo> absolutePath;
    private List<RequestInfo> restFulPath;*/
    private MethodHandlerParam handlerParam = new MethodHandlerParam();
    public static Map<String, CacheRequestHandler> cacheRequestHandler = new HashMap<>();

    public ControllerRequestHandler(ApplicationContext applicationContext) {
        super(applicationContext);

    }



    /*public ControllerRequestHandler(HashMap<String, RequestInfo> absolutePath, List<RequestInfo> restFulPath, ApplicationContext applicationContext) {
        this.absolutePath = absolutePath;
        this.applicationContext = applicationContext;
        this.restFulPath = restFulPath;
    }*/


    public boolean handler(FinalServerContext context) throws Exception {
        String reqURL = context.getRequest().getHttpMethod().name() + "_" + context.getRequest().getPath();
        RequestInfo requestInfo = absolutePath.get(reqURL);
        Map<String, String> matcherRestFul = null;
        if (requestInfo == null) {// 绝对路径匹配为空时，匹配 rest ful
            for (RequestInfo info : restFulPath) {
                if (info.getRequestMethod().name().equals(context.getRequest().getHttpMethod().name())) {
                    matcherRestFul = MatchUtils.matcherRestFul(info.getPath(), context.getRequest().getPath(), info.getRestFulParam());
                    if (matcherRestFul != null) {
                        requestInfo = info;
                        break;
                    }
                }
            }
        }
        if (requestInfo != null) {
            Object result = null;
            if (requestInfo.getBeanName() == null) {// 自定义的请求处理
                requestInfo.getCustomRequestHandler().handler(context);
                return true;
            }

            // rest ful 请求
            if (matcherRestFul != null) {
                Object bean = applicationContext.getBean(requestInfo.getBeanName());
                Method method = bean.getClass().getDeclaredMethod(requestInfo.getMethodName(), requestInfo.getParamType());
                Object[] param = joinRestFulParam(requestInfo, context, matcherRestFul);
                result = method.invoke(bean, param);
            } else { // 绝对路径请求
                // 缓存
                CacheRequestHandler handler = cacheRequestHandler.get(reqURL);
                if (handler == null) {
                    // 没有时从上下文中获取
                    Object bean = applicationContext.getBean(requestInfo.getBeanName());
                    Method method = bean.getClass().getDeclaredMethod(requestInfo.getMethodName(), requestInfo.getParamType());
                    handler = new CacheRequestHandler(bean, method);
                    cacheRequestHandler.put(reqURL, handler);
                }

                Object bean = applicationContext.getBean(requestInfo.getBeanName(), requestInfo.getControllerClass());

                Method declaredMethod = bean.getClass().getDeclaredMethod(requestInfo.getMethodName(),
                        requestInfo.getParamType());

                result = declaredMethod.invoke(bean, new Object[]{FinalServerContext.currentContext().getResponse(), "asdasd", FinalServerContext.currentContext().getRequest()});

//                Object[] param = joinParam(requestInfo, context);
//                result = handler.getMethod().invoke(handler.getBean(), param);
            }

            // 结果处理 ----------------------------------------------------------------------------------------------
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
                if (TypeUtils.isBaseType(result.getClass())) {
                    // 基础类型
                    context.getResponse().returnString(result.toString());
                } else {
                    // json
                    context.getResponse().returnJsonObject(result);
                }
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

    private Object[] joinRestFulParam(RequestInfo handler, FinalServerContext context, Map<String, String> matcherRestFul) {
        if (handler.getParamType().length == 0)
            return handler.getParamType();
        Object[] params = new Object[handler.getParamType().length];
        for (int i = 0; i < handler.getParamType().length; i++) {
            String param = matcherRestFul.get(handler.getParamName()[i]);
            if (param != null) {
                params[i] = TypeUtils.stringToObject(param, handler.getParamType()[i]);
            } else {
                params[i] = handlerParam.match(handler.getParamName()[i], handler.getParamType()[i], context);
            }
        }
        return params;
    }
}
