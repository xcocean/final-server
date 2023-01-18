package top.lingkang.finalserver.server.web.entity;

import top.lingkang.finalserver.server.web.handler.CustomRequestHandler;
import top.lingkang.finalserver.server.web.http.RequestMethod;

import java.util.Arrays;

/**
 * @author lingkang
 * Created by 2022/12/7
 * @since 1.0.0
 */
public class RequestInfo {
    private String path;
    private String[] restFulParam;
    private String beanName;
    private String methodName;
    private Class<?>[] paramType;
    private String[] paramName;
    private Class<?> returnType;
    private RequestMethod requestMethod;
    private CustomRequestHandler customRequestHandler;
    private Class<?> controllerClass;

    public Class<?> getControllerClass() {
        return controllerClass;
    }

    public void setControllerClass(Class<?> controllerClass) {
        this.controllerClass = controllerClass;
    }

    public String[] getRestFulParam() {
        return restFulParam;
    }

    public void setRestFulParam(String[] restFulParam) {
        this.restFulParam = restFulParam;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public CustomRequestHandler getCustomRequestHandler() {
        return customRequestHandler;
    }

    public void setCustomRequestHandler(CustomRequestHandler customRequestHandler) {
        this.customRequestHandler = customRequestHandler;
    }

    public RequestMethod getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(RequestMethod requestMethod) {
        this.requestMethod = requestMethod;
    }

    public Class<?> getReturnType() {
        return returnType;
    }

    public void setReturnType(Class<?> returnType) {
        this.returnType = returnType;
    }

    public String[] getParamName() {
        return paramName;
    }

    public void setParamName(String[] paramName) {
        this.paramName = paramName;
    }

    public Class<?>[] getParamType() {
        return paramType;
    }

    public void setParamType(Class<?>[] paramType) {
        this.paramType = paramType;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    @Override
    public String toString() {
        return "RequestInfo{" +
                "path='" + path + '\'' +
                ", restFulParam=" + Arrays.toString(restFulParam) +
                ", beanName='" + beanName + '\'' +
                ", methodName='" + methodName + '\'' +
                ", paramType=" + Arrays.toString(paramType) +
                ", paramName=" + Arrays.toString(paramName) +
                ", returnType=" + returnType +
                ", requestMethod=" + requestMethod +
                ", customRequestHandler=" + customRequestHandler +
                '}';
    }
}
