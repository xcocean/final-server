package top.lingkang.finalserver.server.utils;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import top.lingkang.finalserver.server.core.FinalServerConfiguration;
import top.lingkang.finalserver.server.core.WebListener;
import top.lingkang.finalserver.server.web.http.FinalServerContext;
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

    public static void pushWebListenerBefore(FinalServerContext context) throws Exception {
        for (WebListener listener : FinalServerConfiguration.webListener) {
            listener.before(context);
        }
    }

    public static void pushWebListenerAfter() throws Exception {
        for (WebListener listener : FinalServerConfiguration.webListener) {
            listener.after();
        }
    }
}
