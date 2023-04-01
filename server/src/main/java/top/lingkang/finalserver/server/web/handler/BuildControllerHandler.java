package top.lingkang.finalserver.server.web.handler;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import top.lingkang.finalserver.server.annotation.*;
import top.lingkang.finalserver.server.constant.FinalServerConstants;
import top.lingkang.finalserver.server.core.CustomRequestHandler;
import top.lingkang.finalserver.server.utils.BeanUtils;
import top.lingkang.finalserver.server.utils.MatchUtils;
import top.lingkang.finalserver.server.utils.TypeUtils;
import top.lingkang.finalserver.server.web.entity.RequestInfo;
import top.lingkang.finalserver.server.web.http.RequestMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author lingkang
 * Created by 2022/12/7
 * @since 1.0.0
 * spring初始化完成后，将会在此构建静态资源文件映射、controller请求处理等
 */
class BuildControllerHandler {
    private static final Logger log = LoggerFactory.getLogger(BuildControllerHandler.class);
    @Autowired
    protected ApplicationContext applicationContext;
    protected HashMap<String, RequestInfo> absolutePath = new HashMap<>();
    protected List<RequestInfo> restFulPath = new ArrayList<>();
    protected boolean isBuild = false;

    public void build() {
        if (isBuild) {
            log.warn("已经构建了请求，此次操作将忽略");
            return;
        }

        log.debug("开始加载 Controller 请求处理");
        String[] allName = applicationContext.getBeanNamesForAnnotation(Controller.class);
        // 兼容spring的controller注解
        String[] names = applicationContext.getBeanNamesForAnnotation(org.springframework.stereotype.Controller.class);
        Set<String> tmp = new HashSet<>(Arrays.asList(allName));
        tmp.addAll(Arrays.asList(names));
        allName = tmp.toArray(new String[]{});

        // 遍历缓存处理
        for (String name : allName) {
            Object bean = BeanUtils.getTarget(applicationContext.getBean(name));
            log.debug(BeanUtils.getSpringProxyBeanName(bean.getClass()));

            String basePath = "";
            // 判断类上是否有@RequestMapping注解
            RequestMapping requestMapping = bean.getClass().getAnnotation(RequestMapping.class);
            if (requestMapping != null) {
                basePath = checkPrefixAndSuffix(requestMapping.value());
            }

            Method[] methods = bean.getClass().getDeclaredMethods();
            for (int index = 0; index < methods.length; index++) {
                Method method = methods[index];
                RequestInfo info = new RequestInfo();
                RequestType requestType = getAnnotationPathValue(method);
                if (requestType != null) {
                    String path = basePath + checkMapping(requestType.value);
                    if (!path.startsWith("/"))
                        path = "/" + path;
                    if (path.length() > 1 && path.endsWith("/"))
                        throw new IllegalArgumentException("Controller处理的URL不能以 '/' 作为结尾  class:" + bean.getClass().getName() + " 方法:" + method.getName());

                    info.setBeanName(name);
                    info.setControllerClass(bean.getClass());
                    info.setRequestMethod(requestType.requestMethod.name());
                    info.setParamNames(getParamNames(method));
                    info.setParamTypes(method.getParameterTypes());
                    info.setParamAnnotation(getParamAnnotation(method));
                    info.setMethod(method);

                    path = path.replaceAll(" ", "");// 删除空格
                    // REST ful API
                    if (path.contains("{")) {
                        info.setPath(path);
                        if (restFulPathCheck(info)) {// GET_/index
                            throw new IllegalArgumentException("存在重复的 Restful API 处理：" + path + "  "
                                    + requestType.requestMethod.name() + "  " + bean.getClass().getName()
                                    + "." + info.getMethod().getName());
                        }
                        String[] fulParam = MatchUtils.getRestFulParam(path);
                        info.setRestFulParam(fulParam);
                        restFulPath.add(info);
                    } else {
                        if (absolutePath.containsKey(requestType.requestMethod.name() + "_" + path)) {// GET_/index
                            throw new IllegalArgumentException("存在重复的URL处理：" + path + "  "
                                    + requestType.requestMethod.name() + "  " + bean.getClass().getName()
                                    + "." + info.getMethod().getName());
                        }
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
        isBuild = true;
    }

    private String[] getParamNames(Method method) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length == 0)
            return FinalServerConstants.EMPTY_STRING_ARRAYS;

        String[] names = new String[parameterTypes.length];
        Annotation[][] annotations = method.getParameterAnnotations();
        for (int i = 0; i < parameterTypes.length; i++) {
            Annotation[] annotation = annotations[i];
            if (annotation.length == 0) {
                String name = TypeUtils.tryGetParamName(i, method);
                if (StrUtil.isEmpty(name)) {
                    log.warn("无法获取到方法入参的名称：" + method.getName() + " 可能无法接受到入参，可以添加对应的注解获取：@RequestParam、@RequestHeader获取入参");
                }
                names[i] = name;
            }
            for (Annotation ann : annotation) {
                String name = TypeUtils.getRequestParamName(ann);
                names[i] = name;
            }
        }
        return names;
    }

    private Annotation[] getParamAnnotation(Method method) {
        Annotation[][] annotations = method.getParameterAnnotations();
        Annotation[] result = new Annotation[annotations.length];
        for (int i = 0; i < annotations.length; i++) {
            for (Annotation a : annotations[i]) {
                if (a instanceof RequestParam || a instanceof RequestHeader) {
                    result[i] = a;
                    break;
                }
            }
        }
        return result;
    }

    // requestType.requestMethod.name() + "_" + path
    private boolean restFulPathCheck(RequestInfo requestInfo) {
        for (RequestInfo info : restFulPath) {
            if (info.getPath().equals(requestInfo.getPath()) && info.getRequestMethod().equals(requestInfo.getRequestMethod()))
                return true;
        }
        return false;
    }

    public void addRequestHandler(String path, RequestMethod method, CustomRequestHandler handler) {
        Assert.notBlank(path, "处理路径不能为空");
        Assert.notNull(method, "请求方法不能为空");
        Assert.notNull(handler, "自定义处理不能为空");
        if (!path.startsWith("/"))
            path = "/" + path;

        if (absolutePath.containsKey(method.name() + "_" + path))
            throw new IllegalArgumentException("已经存在的处理方法：" + method.name() + "_" + path);

        RequestInfo info = new RequestInfo();
        info.setCustomRequestHandler(handler);
        info.setCustomRequestHandler(true);
        info.setPath(path);
        info.setRequestMethod(method.name());
        absolutePath.put(method.name() + "_" + path, info);
        log.debug("添加请求处理成功");
    }

    // @RequestMapping 检查前后缀,返回值最后不带 /
    private String checkPrefixAndSuffix(String path) {
        if (path.length() == 0)
            return path;
        if (!path.startsWith("/"))
            path = "/" + path;
        if (path.endsWith("/"))
            path = path.substring(0, path.length() - 1);
        return path;
    }

    private String checkMapping(String path) {
        if (path.length() > 1 && path.startsWith("/")) {
            path = path.substring(1);
        }
        return path;
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
