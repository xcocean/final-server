package top.lingkang.finalserver.server.web.entity;

import top.lingkang.finalserver.server.core.CustomRequestHandler;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author lingkang
 * Created by 2022/12/7
 * @since 1.0.0
 */
public class RequestInfo {
    private String path;
    private String[] restFulParam;
    private Method method;
    private int paramNum;
    private String requestMethod;
    private Class<?> controllerClass;
    private String beanName;
    private boolean isCustomRequestHandler;
    private CustomRequestHandler customRequestHandler;

    @Override
    public String toString() {
        return "RequestInfo{" +
                "path='" + path + '\'' +
                ", restFulParam=" + Arrays.toString(restFulParam) +
                ", method=" + method +
                ", paramNum=" + paramNum +
                ", requestMethod='" + requestMethod + '\'' +
                ", controllerClass=" + controllerClass +
                ", beanName='" + beanName + '\'' +
                ", isCustomRequestHandler=" + isCustomRequestHandler +
                ", customRequestHandler=" + customRequestHandler +
                '}';
    }

    public CustomRequestHandler getCustomRequestHandler() {
        return customRequestHandler;
    }

    public void setCustomRequestHandler(CustomRequestHandler customRequestHandler) {
        this.customRequestHandler = customRequestHandler;
    }

    public boolean isCustomRequestHandler() {
        return isCustomRequestHandler;
    }

    public void setCustomRequestHandler(boolean customRequestHandler) {
        isCustomRequestHandler = customRequestHandler;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String[] getRestFulParam() {
        return restFulParam;
    }

    public void setRestFulParam(String[] restFulParam) {
        this.restFulParam = restFulParam;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public int getParamNum() {
        return paramNum;
    }

    public void setParamNum(int paramNum) {
        this.paramNum = paramNum;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public Class<?> getControllerClass() {
        return controllerClass;
    }

    public void setControllerClass(Class<?> controllerClass) {
        this.controllerClass = controllerClass;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }
}
