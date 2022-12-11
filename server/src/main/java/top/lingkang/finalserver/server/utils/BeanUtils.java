package top.lingkang.finalserver.server.utils;

import org.springframework.context.ApplicationContext;
import top.lingkang.finalserver.server.FinalServerApplication;

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

    public static <T> T getBean(Class<T> clazz, ApplicationContext applicationContext){
        try {
            return applicationContext.getBean(clazz);
        }catch (Exception e){
            return null;
        }
    }

    public static <T> T getBean(Class<T> clazz){
        try {
            return FinalServerApplication.applicationContext.getBean(clazz);
        }catch (Exception e){
            return null;
        }
    }
}
