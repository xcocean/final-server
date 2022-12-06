package top.lingkang.finalserver.server.utils;

/**
 * @author lingkang
 * Created by 2022/12/7
 */
public class ProxyUtils {
    public static Class<?> getSpringProxyToClass(Class<?> clazz) {
        String name = clazz.getName().split("\\$\\$")[0];
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
