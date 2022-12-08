package top.lingkang.finalserver.server.web.handler;

import cn.hutool.core.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import top.lingkang.finalserver.server.annotation.Controller;
import top.lingkang.finalserver.server.annotation.GET;
import top.lingkang.finalserver.server.utils.MatchUtils;
import top.lingkang.finalserver.server.utils.ProxyBeanUtils;
import top.lingkang.finalserver.server.web.entity.RequestHandler;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;

/**
 * @author lingkang
 * Created by 2022/12/7
 */
public class BuildControllerHandler {
    private static final Logger log = LoggerFactory.getLogger(BuildControllerHandler.class);
    private ApplicationContext applicationContext;

    public BuildControllerHandler(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    private HashMap<String, RequestHandler> map = new HashMap<>();

    public ControllerHandler build() {
        log.debug("开始加载 Controller 请求处理");
        String[] allName = applicationContext.getBeanDefinitionNames();
        for (String name : allName) {
            Object bean = applicationContext.getBean(name);
            Controller annotation = bean.getClass().getAnnotation(Controller.class);
            if (annotation == null)
                continue;
            log.debug(ProxyBeanUtils.getSpringProxyBeanName(bean.getClass()));

            Method[] methods = bean.getClass().getDeclaredMethods();
            for (Method method : methods) {

                RequestHandler handler = new RequestHandler();
                String path = null;
                String matchPath = null;
                GET get = method.getAnnotation(GET.class);
                if (get != null) {
                    if (StrUtil.isNotBlank(get.path()) && !MatchUtils.hasMatching(get.path()))
                        path = get.path();
                    else if (MatchUtils.hasMatching(get.path()))
                        matchPath = get.path();
                    else
                        path = "/";

                    handler.setBeanName(name);
                    handler.setMethodName(method.getName());
                    handler.setReturnType(method.getReturnType());
                    handler.setParamName(toParamName(method.getParameters()));
                    handler.setParamType(method.getParameterTypes());
                    map.put(path, handler);
                }

            }
        }
        log.debug("Controller 请求处理加载完成");
        return new ControllerHandler(map, applicationContext);
    }

    private String[] toParamName(Parameter[] parameters) {
        String[] strings = new String[parameters.length];
        for (int i = 0; i < strings.length; i++) {
            strings[i] = parameters[i].getName();
        }
        return strings;
    }
}
