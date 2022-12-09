package top.lingkang.finalserver.server.utils;

/**
 * @author lingkang
 * Created by 2022/12/7
 * @since 1.0.0
 */
public class ProxyBeanUtils {
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
}
