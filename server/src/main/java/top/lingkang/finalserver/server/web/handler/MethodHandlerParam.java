package top.lingkang.finalserver.server.web.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.lingkang.finalserver.server.utils.CommonUtils;
import top.lingkang.finalserver.server.utils.TypeUtils;
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
    private static final Map<Class<?>, Object> initValue = new HashMap<>();


    public Object match(String name, Class<?> type, FinalServerContext context) {
        HandlerParam handlerParam = handlerMap.get(type);
        if (handlerParam == null) {
            // 没有的处理类型, 尝试对象转换
            return CommonUtils.paramToBean(name, type, context);
        }
        Object handler = handlerParam.handler(name, type, context);
        if (handler == null)
            return initValue.get(type);
        return handler;
    }

    interface HandlerParam {
        Object handler(String name, Class<?> type, FinalServerContext context);
    }

    HandlerParam baseHandlerParam;

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
        HandlerParam session = new HandlerParam() {
            @Override
            public Object handler(String name, Class<?> type, FinalServerContext context) {
                return context.getRequest().getSession();
            }
        };
        handlerMap.put(HttpSession.class, session);
        handlerMap.put(Session.class, session);

        HandlerParam response = new HandlerParam() {
            @Override
            public Object handler(String name, Class<?> type, FinalServerContext context) {
                return context.getResponse();
            }
        };
        handlerMap.put(Response.class, response);
        handlerMap.put(HttpResponse.class, response);

        baseHandlerParam = new HandlerParam() {
            @Override
            public Object handler(String name, Class<?> type, FinalServerContext context) {
                String param = context.getRequest().getParam(name);
                if (param == null)
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
