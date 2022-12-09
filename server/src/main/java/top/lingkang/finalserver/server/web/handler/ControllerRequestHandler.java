package top.lingkang.finalserver.server.web.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import top.lingkang.finalserver.server.web.entity.RequestInfo;
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
    private HashMap<String, RequestInfo> absolutePath = new HashMap<>();
    private ApplicationContext applicationContext;
    private MethodHandlerParam handlerParam = new MethodHandlerParam();

    public ControllerRequestHandler(HashMap<String, RequestInfo> absolutePath, ApplicationContext applicationContext) {
        this.absolutePath = absolutePath;
        this.applicationContext = applicationContext;
    }

    public boolean handler(FinalServerContext context) throws Exception {
        RequestInfo requestInfo = absolutePath.get(context.getRequest().getHttpMethod().name() + "_" + context.getRequest().getPath());
        if (requestInfo != null) {
            if (requestInfo.getBeanName()==null){// 自定义的请求处理
                requestInfo.getCustomRequestHandler().handler(context);
                return true;
            }
            Object bean = applicationContext.getBean(requestInfo.getBeanName());
            Method method = bean.getClass().getDeclaredMethod(requestInfo.getMethodName(), requestInfo.getParamType());
            Object result = method.invoke(bean, joinParam(requestInfo, context));
            if (result == null)
                return true;
            if (context.getResponse().isReady()) {
                log.warn("已经处理了输出流，本次忽略return值： " + bean.getClass().getSimpleName() + " " + method.getName());
                return true;
            }
            // 其他结果返回toString
            context.getResponse().returnString(result.toString());
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
