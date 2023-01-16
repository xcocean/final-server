package top.lingkang.finalserver.server.web.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import top.lingkang.finalserver.server.annotation.*;
import top.lingkang.finalserver.server.utils.BeanUtils;
import top.lingkang.finalserver.server.utils.MatchUtils;
import top.lingkang.finalserver.server.web.entity.RequestInfo;
import top.lingkang.finalserver.server.web.http.RequestMethod;

import java.lang.reflect.Method;
import java.util.*;

/**
 * @author lingkang
 * Created by 2022/12/7
 * @since 1.0.0
 * spring初始化完成后，将会在此构建静态资源文件映射、controller请求处理等
 */
public class BuildControllerHandler {
    private static final Logger log = LoggerFactory.getLogger(BuildControllerHandler.class);
    private ApplicationContext applicationContext;

    public BuildControllerHandler(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    private HashMap<String, RequestInfo> absolutePath = new HashMap<>();
    private List<RequestInfo> restFulPath = new ArrayList<>();

    public ControllerRequestHandler build() {
        log.debug("开始加载 Controller 请求处理");
        String[] allName = applicationContext.getBeanNamesForAnnotation(Controller.class);
        // 兼容spring的controller注解
        String[] names = applicationContext.getBeanNamesForAnnotation(org.springframework.stereotype.Controller.class);
        Set<String> tmp = new HashSet<>(Arrays.asList(allName));
        tmp.addAll(Arrays.asList(names));
        allName = tmp.toArray(new String[]{});
        for (String name : allName) {
            Object bean = BeanUtils.getTarget(applicationContext.getBean(name));
            Object annotation = bean.getClass().getAnnotation(Controller.class);
            if (annotation == null)
                annotation = bean.getClass().getAnnotation(org.springframework.stereotype.Controller.class);
            if (annotation == null)
                continue;
            log.debug(BeanUtils.getSpringProxyBeanName(bean.getClass()));

            String basePath = "/";
            // 判断类上是否有@RequestMapping注解
            RequestMapping requestMapping = bean.getClass().getAnnotation(RequestMapping.class);
            if (requestMapping != null) {
                basePath = checkPrefixAndSuffix(requestMapping.value());
            }

            Method[] methods = bean.getClass().getDeclaredMethods();
            for (Method method : methods) {
                RequestInfo info = new RequestInfo();
                RequestType requestType = getAnnotationPathValue(method);
                if (requestType != null) {
                    String path = basePath + checkMapping(requestType.value);
                    if (path.length() > 1 && path.endsWith("/"))
                        throw new IllegalArgumentException("Controller处理的URL不能以 '/' 作为结尾  class:" + bean.getClass().getName() + " 方法:" + method.getName());

                    if (absolutePath.containsKey(requestType.requestMethod.name() + "_" + path)) {// GET_/index
                        throw new IllegalArgumentException("存在重复的URL处理：" + path + "  " + requestType.requestMethod.name() + "  " + bean.getClass().getName());
                    }

                    info.setRequestMethod(requestType.requestMethod);
                    info.setBeanName(name);
                    info.setMethodName(method.getName());
                    info.setReturnType(method.getReturnType());
                    info.setParamName(getParamNames(method.getName(), bean.getClass(), method.getParameterTypes()));
                    info.setParamType(method.getParameterTypes());
                    // REST ful API
                    if (path.contains("{")) {
                        path = path.replaceAll(" ", "");
                        String[] fulParam = MatchUtils.getRestFulParam(path);
                        info.setRestFulParam(fulParam);
                        info.setPath(path);
                        restFulPath.add(info);
                    } else {
                        // 方法名_path
                        path = requestType.requestMethod.name() + "_" + path;
                        info.setPath(path);
                        absolutePath.put(path, info);
                    }
                }
            }
        }
        if (log.isDebugEnabled()) {
            for (Map.Entry<String, RequestInfo> entry : absolutePath.entrySet())
                log.debug(entry.getKey());
            for (RequestInfo name : restFulPath)
                log.debug(name.toString());
        }
        log.debug("Controller 请求处理加载完成");
        return new ControllerRequestHandler(absolutePath, restFulPath, applicationContext);
    }

    // @RequestMapping 检查前后缀
    private String checkPrefixAndSuffix(String path) {
        if (!path.startsWith("/"))
            path = "/" + path;
        if (!path.endsWith("/"))
            path = path + "/";
        return path;
    }

    private String checkMapping(String path) {
        if (path.length() > 1 && path.startsWith("/")) {
            path = path.substring(1);
        }
        return path;
    }

    private String[] getParamNames(String methodName, Class<?> clazz, Class<?>... parameterTypes) {
        try {
            Method method = clazz.getMethod(methodName, parameterTypes);
            return new LocalVariableTableParameterNameDiscoverer().getParameterNames(method);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new String[0];
    }

    private RequestType getAnnotationPathValue(Method method) {
        GET get = method.getAnnotation(GET.class);
        if (get != null)
            return new RequestType(get.value(), RequestMethod.GET);

        POST post = method.getAnnotation(POST.class);
        if (post != null)
            return new RequestType(post.value(), RequestMethod.POST);

        RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
        if (requestMapping != null)
            return new RequestType(requestMapping.value(), requestMapping.method());


        DELETE delete = method.getAnnotation(DELETE.class);
        if (delete != null)
            return new RequestType(delete.value(), RequestMethod.DELETE);

        PUT put = method.getAnnotation(PUT.class);
        if (put != null)
            return new RequestType(put.value(), RequestMethod.PUT);

        return null;
    }

    class RequestType {
        public RequestType(String value, RequestMethod requestMethod) {
            this.value = value;
            this.requestMethod = requestMethod;
        }

        public String value;
        public RequestMethod requestMethod;

        @Override
        public String toString() {
            return "RequestType{" +
                    "value='" + value + '\'' +
                    ", requestMethod=" + requestMethod +
                    '}';
        }
    }
}
