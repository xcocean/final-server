package top.lingkang.finalserver.server.utils;

import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import top.lingkang.finalserver.server.FinalServerApplication;

import java.lang.reflect.Field;

/**
 * @author lingkang
 * Created by 2022/12/7
 * @since 1.0.0
 */
public class BeanUtils {
    public static Class<?> getSpringProxyToClass(Class<?> clazz) {
        try {
            return Class.forName(getSpringProxyBeanName(clazz));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getSpringProxyBeanName(Class<?> clazz) {
        return clazz.getName().split("\\$\\$")[0];
    }

    public static <T> T getBean(Class<T> clazz, ApplicationContext applicationContext) {
        try {
            return applicationContext.getBean(clazz);
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> T getBean(Class<T> clazz) {
        try {
            return FinalServerApplication.applicationContext.getBean(clazz);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取一个代理实例的真实对象
     */
    public static Object getTarget(Object proxy) {
        try {
            if (!AopUtils.isAopProxy(proxy)) {
                return proxy;
            }
            if (AopUtils.isJdkDynamicProxy(proxy)) {
                /**JDK动态代理*/
                return getJdkDynamicProxyTargetObject(proxy);
            } else {
                /**cglib动态代理*/
                return getCglibProxyTargetObject(proxy);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Object getCglibProxyTargetObject(Object proxy) throws Exception {
        Field h = proxy.getClass().getDeclaredField("CGLIB$CALLBACK_0");
        h.setAccessible(true);
        Object dynamicAdvisedInterceptor = h.get(proxy);
        Field advised = dynamicAdvisedInterceptor.getClass().getDeclaredField("advised");
        advised.setAccessible(true);
        Object target = ((AdvisedSupport) advised.get(dynamicAdvisedInterceptor)).getTargetSource().getTarget();
        return target;
    }

    private static Object getJdkDynamicProxyTargetObject(Object proxy) throws Exception {
        Field h = proxy.getClass().getSuperclass().getDeclaredField("h");
        h.setAccessible(true);
        AopProxy aopProxy = (AopProxy) h.get(proxy);
        Field advised = aopProxy.getClass().getDeclaredField("advised");
        advised.setAccessible(true);
        Object target = ((AdvisedSupport) advised.get(aopProxy)).getTargetSource().getTarget();
        return target;
    }

    /**
     * 获取代理对象的切面包装,里面有
     * List<Advisor> advisors  所有的advisor
     * TargetSource targetSource   目标实例通过getTarget()方法获取
     */
    public static AdvisedSupport getAdvisedSupport(Object proxy) throws Exception {
        Field h;
        if (AopUtils.isJdkDynamicProxy(proxy)) {
            h = proxy.getClass().getSuperclass().getDeclaredField("h");
        } else {
            h = proxy.getClass().getDeclaredField("CGLIB$CALLBACK_0");
        }
        h.setAccessible(true);
        Object dynamicAdvisedInterceptor = h.get(proxy);
        Field advised = dynamicAdvisedInterceptor.getClass().getDeclaredField("advised");
        advised.setAccessible(true);
        return (AdvisedSupport) advised.get(dynamicAdvisedInterceptor);
    }

}
