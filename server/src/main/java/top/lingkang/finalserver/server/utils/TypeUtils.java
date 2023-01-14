package top.lingkang.finalserver.server.utils;

/**
 * @author lingkang
 * Created by 2023/1/14
 */
public class TypeUtils {
    public static Object stringToObject(String str, Class<?> type) {
        if (type == String.class)
            return str;

        if (type == Integer.class || type == int.class)
            return Integer.parseInt(str);
        else if (type == Long.class || type == long.class)
            return Long.parseLong(str);
        else if (type == Double.class || type == double.class)
            return Double.parseDouble(str);
        else if (type == Float.class || type == float.class)
            return Float.parseFloat(str);
        return null;
    }
}
