package top.lingkang.finalserver.server.utils;

import cn.hutool.core.util.StrUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lingkang
 * Created by 2022/12/7
 * @since 1.0.0
 */
public class MatchUtils {

    public static Map<String, String> matcherRestFul(String pattern, String path, String[] param) {
        String[] root = pattern.split("/");
        String[] node = path.split("/");
        if (root.length != node.length)
            return null;
        Map<String, String> map = new HashMap<>();
        int index = 0;
        for (int i = 1; i < root.length; i++) {
            if (root[i].contains("{")) {
                map.put(param[index], node[i]);
                index++;
            } else if (!root[i].equals(node[i])) {
                return null;
            }
        }
        return map;
    }

    public static String[] getRestFulParam(String path) {
        List<String> list = new ArrayList<>();
        int start = path.indexOf("{");
        if (start != -1) {
            int end = path.indexOf("}", start + 1);
            if (end == -1) {
                StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
                for (StackTraceElement element : stackTrace) {
                    System.out.println(element.getClassName() + "====" + element.getMethodName());
                }
                throw new RuntimeException("{ 与 } 的个数不匹配，请检查 rest full api 的路径设置是否正确: " + path);
            }

            list.add(StrUtil.trimToEmpty(path.substring(start + 1, end)));
            for (; ; ) {
                start = path.indexOf("{", end);
                if (start == -1)
                    break;
                end = path.indexOf("}", start + 1);
                if (end == -1)
                    throw new RuntimeException("error");
                list.add(StrUtil.trimToEmpty(path.substring(start + 1, end)));
            }
        }
        return list.toArray(new String[0]);
    }
}
