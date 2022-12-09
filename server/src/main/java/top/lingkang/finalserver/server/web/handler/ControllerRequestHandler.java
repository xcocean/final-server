package top.lingkang.finalserver.server.web.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import top.lingkang.finalserver.server.web.http.FinalServerContext;

import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * @author lingkang
 * Created by 2022/12/7
 * @since 1.0.0
 */
public class ControllerRequestHandler implements RequestHandler {
    private static final Logger log = LoggerFactory.getLogger(ControllerRequestHandler.class);
    private HashMap<String, top.lingkang.finalserver.server.web.entity.RequestHandler> handler = new HashMap<>();
    private ApplicationContext applicationContext;
    private MethodHandlerParam handlerParam = new MethodHandlerParam();

    public ControllerRequestHandler(HashMap<String, top.lingkang.finalserver.server.web.entity.RequestHandler> handler, ApplicationContext applicationContext) {
        this.handler = handler;
        this.applicationContext = applicationContext;
    }

    public boolean handler(FinalServerContext context) throws Exception {
        top.lingkang.finalserver.server.web.entity.RequestHandler requestHandler = handler.get(context.getRequest().getPath());
        if (requestHandler != null) {
            Object bean = applicationContext.getBean(requestHandler.getBeanName());
            Method method = bean.getClass().getDeclaredMethod(requestHandler.getMethodName(), requestHandler.getParamType());
            Object result = method.invoke(bean, joinParam(requestHandler, context));
            if (result == null)
                return true;
            if (context.getResponse().isReady()) {
                log.warn("已经处理了输出流，本次忽略return值： " + bean.getClass().getSimpleName() + " " + method.getName());
                return true;
            }
            if (result.getClass() == String.class) {
                context.getResponse().returnString(result.toString());
            }
        }
        return true;
    }

    private Object[] joinParam(top.lingkang.finalserver.server.web.entity.RequestHandler handler, FinalServerContext context) {
        if (handler.getParamType().length == 0)
            return handler.getParamType();
        Object[] params = new Object[handler.getParamType().length];
        for (int i = 0; i < handler.getParamType().length; i++) {
            params[i] = handlerParam.match(handler.getParamName()[i], handler.getParamType()[i], context);
        }
        return params;
    }
}
