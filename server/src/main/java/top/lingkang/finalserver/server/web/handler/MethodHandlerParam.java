package top.lingkang.finalserver.server.web.handler;

import cn.hutool.core.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.lingkang.finalserver.server.annotation.RequestHeader;
import top.lingkang.finalserver.server.utils.CommonUtils;
import top.lingkang.finalserver.server.utils.TypeUtils;
import top.lingkang.finalserver.server.web.http.*;

import java.lang.annotation.Annotation;
import java.util.HashMap;

/**
 * @author lingkang
 * Created by 2022/12/7
 * @since 1.0.0
 */
public class MethodHandlerParam {
    private static final Logger log = LoggerFactory.getLogger(MethodHandlerParam.class);
    public static final HashMap<Class<?>, HandlerParam> handlerMap = new HashMap<>();


    public Object match(String name, Class<?> type, Annotation annotation, FinalServerContext context) {
        HandlerParam handlerParam = handlerMap.get(type);
        if (handlerParam == null) {
            // 没有的处理类型, 尝试对象转换
            return CommonUtils.paramToBean(name, type, annotation, context);
        }
        Object handler = handlerParam.handler(name, type, annotation, context);
        if (handler == null)
            return TypeUtils.initValue.get(type);
        return handler;
    }

    interface HandlerParam {
        Object handler(String name, Class<?> type, Annotation annotation, FinalServerContext context);
    }

    HandlerParam baseHandlerParam;

    public MethodHandlerParam() {
        handlerMap.put(FinalServerContext.class, new HandlerParam() {
            @Override
            public Object handler(String name, Class<?> type, Annotation annotation, FinalServerContext context) {
                return context;
            }
        });
        HandlerParam request = new HandlerParam() {
            @Override
            public Object handler(String name, Class<?> type, Annotation annotation, FinalServerContext context) {
                return context.getRequest();
            }
        };
        handlerMap.put(Request.class, request);
        handlerMap.put(HttpRequest.class, request);

        // session
        HandlerParam session = new HandlerParam() {
            @Override
            public Object handler(String name, Class<?> type, Annotation annotation, FinalServerContext context) {
                return context.getRequest().getSession();
            }
        };
        handlerMap.put(HttpSession.class, session);
        handlerMap.put(Session.class, session);

        HandlerParam response = new HandlerParam() {
            @Override
            public Object handler(String name, Class<?> type, Annotation annotation, FinalServerContext context) {
                return context.getResponse();
            }
        };
        handlerMap.put(Response.class, response);
        handlerMap.put(HttpResponse.class, response);

        baseHandlerParam = new HandlerParam() {
            @Override
            public Object handler(String name, Class<?> type, Annotation annotation, FinalServerContext context) {
                if (annotation instanceof RequestHeader) {// 请求头中获取
                    String param = context.getRequest().getHeader(name);
                    if (StrUtil.isEmpty(param))
                        return null;
                    return TypeUtils.stringToObject(param, type);
                }// 请求体、请求参数中获取
                String param = context.getRequest().getParam(name);
                if (StrUtil.isEmpty(param))
                    return null;
                try {
                    return TypeUtils.stringToObject(param, type);
                } catch (Exception e) {
                    log.error("请求入参错误，参数名：" + name + "  请检查入参类型与接收类型是否正确");
                    throw new IllegalArgumentException(e);
                }
            }
        };
        handlerMap.put(String.class, baseHandlerParam);
        handlerMap.put(Integer.class, baseHandlerParam);
        handlerMap.put(int.class, baseHandlerParam);
        handlerMap.put(Long.class, baseHandlerParam);
        handlerMap.put(long.class, baseHandlerParam);
        handlerMap.put(Double.class, baseHandlerParam);
        handlerMap.put(double.class, baseHandlerParam);
        handlerMap.put(Float.class, baseHandlerParam);
        handlerMap.put(float.class, baseHandlerParam);

    }
}
