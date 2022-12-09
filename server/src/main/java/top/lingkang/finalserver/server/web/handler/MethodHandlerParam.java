package top.lingkang.finalserver.server.web.handler;

import top.lingkang.finalserver.server.web.http.*;

import java.util.HashMap;

/**
 * @author lingkang
 * Created by 2022/12/7
 * @since 1.0.0
 */
public class MethodHandlerParam {
    public static final HashMap<Class<?>, HandlerParam> handlerMap = new HashMap<>();

    public Object match(String name, Class<?> type, FinalServerContext context) {
        HandlerParam handlerParam = handlerMap.get(type);
        if (handlerParam != null)
            return handlerParam.handler(name, type, context);
        return null;
    }

    public interface HandlerParam {
        Object handler(String name, Class<?> type, FinalServerContext context);
    }

    public MethodHandlerParam() {
        HandlerParam request = new HandlerParam() {
            @Override
            public Object handler(String name, Class<?> type, FinalServerContext context) {
                return context.getRequest();
            }
        };
        handlerMap.put(Request.class, request);
        handlerMap.put(HttpRequest.class, request);

        HandlerParam response = new HandlerParam() {
            @Override
            public Object handler(String name, Class<?> type, FinalServerContext context) {
                return context.getResponse();
            }
        };
        handlerMap.put(Response.class, response);
        handlerMap.put(HttpResponse.class, response);

        HandlerParam base = new HandlerParam() {
            @Override
            public Object handler(String name, Class<?> type, FinalServerContext context) {
                String param = context.getRequest().getParam(name);
                if (type == String.class)
                    return param;
                else if (type == Integer.class)
                    return Integer.valueOf(param);
                else if (type == Long.class)
                    return Long.valueOf(param);
                else if (type == Double.class)
                    return Double.valueOf(param);
                else if (type == Float.class)
                    return Float.class;
                return null;
            }
        };
        handlerMap.put(String.class, base);
        handlerMap.put(Integer.class, base);
        handlerMap.put(Long.class, base);
        handlerMap.put(Double.class, base);
        handlerMap.put(Float.class, base);

    }
}
