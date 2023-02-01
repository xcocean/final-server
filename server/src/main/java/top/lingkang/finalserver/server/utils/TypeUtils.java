package top.lingkang.finalserver.server.utils;

import cn.hutool.core.convert.BasicType;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import top.lingkang.finalserver.server.annotation.NotNull;
import top.lingkang.finalserver.server.annotation.RequestHeader;
import top.lingkang.finalserver.server.annotation.RequestParam;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lingkang
 * Created by 2023/1/14
 */
public class TypeUtils {
    public static final Map<Class<?>, Object> initValue = new HashMap<>();

    static {
        initValue.put(int.class, 0);
        initValue.put(long.class, 0L);
        initValue.put(double.class, 0d);
        initValue.put(float.class, 0f);
        initValue.put(byte.class, '0');
        initValue.put(short.class, 0);
        initValue.put(char.class, '0');
        initValue.put(boolean.class, false);
    }

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

    private static final DefaultParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    public static String tryGetParamName(int index, Method method) {
        return parameterNameDiscoverer.getParameterNames(method)[index];
    }

    public static String getRequestParamName(Annotation annotation) {
        if (annotation instanceof RequestParam) {
            return ((RequestParam) annotation).value();
        } else if (annotation instanceof RequestHeader) {
            return ((RequestHeader) annotation).value();
        }
        return null;
    }


}
