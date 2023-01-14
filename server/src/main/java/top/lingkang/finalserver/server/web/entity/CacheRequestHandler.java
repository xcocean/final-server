package top.lingkang.finalserver.server.web.entity;

import java.lang.reflect.Method;

/**
 * @author lingkang
 * Created by 2023/1/15
 * @since 1.0.0
 */
public class CacheRequestHandler {
    private Object bean;
    private Method method;

    public CacheRequestHandler(Object bean, Method method) {
        this.bean = bean;
        this.method = method;
    }

    @Override
    public String toString() {
        return "CacheRequestHandler{" +
                "bean=" + bean +
                ", method=" + method +
                '}';
    }

    public Object getBean() {
        return bean;
    }

    public void setBean(Object bean) {
        this.bean = bean;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }
}
