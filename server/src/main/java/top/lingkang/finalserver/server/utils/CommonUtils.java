package top.lingkang.finalserver.server.utils;

import top.lingkang.finalserver.server.web.http.StaticMimes;

/**
 * @author lingkang
 * Created by 2022/12/8
 */
public class CommonUtils {
    public static String getResponseHeadName(String filePath) {
        int index = filePath.lastIndexOf(".");
        if (index == -1)
            return null;
        return StaticMimes.get(filePath.substring(index));
    }
}
