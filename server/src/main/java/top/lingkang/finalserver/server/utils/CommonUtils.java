package top.lingkang.finalserver.server.utils;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import top.lingkang.finalserver.server.web.http.StaticMimes;

/**
 * @author lingkang
 * Created by 2022/12/8
 * @since 1.0.0
 */
public class CommonUtils {
    public static void setResponseHeadName(String filePath, HttpHeaders headers) {
        int index = filePath.lastIndexOf(".");
        if (index == -1)
            return;
        String type = StaticMimes.get(filePath.substring(index));
        if (type != null)
            headers.set(HttpHeaderNames.CONTENT_TYPE, type);
    }
}
