package top.lingkang.finalserver.server.web.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.lingkang.finalserver.server.web.http.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lingkang
 * Created by 2022/12/7
 * @since 1.0.0
 */
public class MethodHandlerParam {
    private static final Logger log = LoggerFactory.getLogger(MethodHandlerParam.class);
    public static final HashMap<Class<?>, HandlerParam> handlerMap = new HashMap<>();
    private static final Map<Class, Object> initValue = new HashMap<>();


    public Object match(String name, Class<?> type, FinalServerContext context) {
        Object handler = handlerMap.get(type).handler(name, type, context);
        if (handler == null)
            return initValue.get(type);
        return handler;
    }

    interface HandlerParam {
        Object handler(String name, Class<?> type, FinalServerContext context);
    }

    public MethodHandlerParam() {
        handlerMap.put(FinalServerContext.class, new HandlerParam() {
            @Override
            public Object handler(String name, Class<?> type, FinalServerContext context) {
                return context;
            }
        });
        HandlerParam request = new HandlerParam() {
            @Override
            public Object handler(String name, Class<?> type, FinalServerContext context) {
                return context.getRequest();
            }
        };
        handlerMap.put(Request.class, request);
        handlerMap.put(HttpRequest.class, request);

        // session
        handlerMap.put(HttpSession.class, new HandlerParam() {
            @Override
            public Object handler(String name, Class<?> type, FinalServerContext context) {
                return context.getRequest().getSession();
            }
        });
        handlerMap.put(Session.class, new HandlerParam() {
            @Override
            public Object handler(String name, Class<?> type, FinalServerContext context) {
                return context.getRequest().getSession();
            }
        });

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
                if (param == null)
                    return null;
                try {
                    if (type == String.class)
                        return param;

                    if (type == Integer.class || type == int.class)
                        return Integer.parseInt(param);
                    else if (type == Long.class || type == long.class)
                        return Long.parseLong(param);
                    else if (type == Double.class || type == double.class)
                        return Double.parseDouble(param);
                    else if (type == Float.class || type == float.class)
                        return Float.parseFloat(param);
                    return null;
                } catch (Exception e) {
                    log.error("请求入参错误，参数名：" + name + "  请检查入参类型与接收类型是否正确");
                    throw new IllegalArgumentException(e);
                }
            }
        };
        handlerMap.put(String.class, base);
        handlerMap.put(Integer.class, base);
        handlerMap.put(int.class, base);
        handlerMap.put(Long.class, base);
        handlerMap.put(long.class, base);
        handlerMap.put(Double.class, base);
        handlerMap.put(double.class, base);
        handlerMap.put(Float.class, base);
        handlerMap.put(float.class, base);

        initValue.put(String.class, null);
        initValue.put(Integer.class, null);
        initValue.put(int.class, 0);
        initValue.put(Long.class, null);
        initValue.put(long.class, 0);
        initValue.put(Double.class, null);
        initValue.put(double.class, 0);
        initValue.put(Float.class, null);
        initValue.put(float.class, 0);
    }
}
