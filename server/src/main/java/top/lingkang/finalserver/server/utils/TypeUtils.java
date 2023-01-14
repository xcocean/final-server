package top.lingkang.finalserver.server.utils;

import cn.hutool.core.convert.BasicType;
import top.lingkang.finalserver.server.annotation.NotNull;

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

    /**
     * 判断是否是基础类型，或其包装类
     */
    public static boolean isBaseType(@NotNull Class<?> clazz) {
        return clazz.isPrimitive() || BasicType.WRAPPER_PRIMITIVE_MAP.containsKey(clazz);
    }
}
