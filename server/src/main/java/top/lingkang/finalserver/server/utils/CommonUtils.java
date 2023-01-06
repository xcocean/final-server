package top.lingkang.finalserver.server.utils;

import cn.hutool.core.io.FileUtil;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import top.lingkang.finalserver.server.core.FinalServerConfiguration;
import top.lingkang.finalserver.server.core.WebListener;
import top.lingkang.finalserver.server.web.entity.ResponseFile;
import top.lingkang.finalserver.server.web.http.FinalServerContext;
import top.lingkang.finalserver.server.web.http.StaticMimes;

import java.net.URLEncoder;

/**
 * @author lingkang
 * Created by 2022/12/8
 * @since 1.0.0
 */
public class CommonUtils {
    public static void setResponseHeadName(ResponseFile responseFile, HttpHeaders headers) throws Exception {
        int index = responseFile.getFilePath().lastIndexOf(".");
        if (index == -1)
            return;

        if (!headers.contains(HttpHeaderNames.CONTENT_TYPE)) {
            String type = StaticMimes.get(responseFile.getFilePath().substring(index));
            headers.set(HttpHeaderNames.CONTENT_TYPE, type);

            // 是否为下载
            if (responseFile.isDownload() && !headers.contains(HttpHeaderNames.CONTENT_DISPOSITION)) {
                if (responseFile.getName() == null)// 设置名称
                    responseFile.setName(FileUtil.getName(responseFile.getFilePath()));
                headers.set(HttpHeaderNames.CONTENT_DISPOSITION, "attachment;filename="
                        + URLEncoder.encode(responseFile.getName(), "UTF-8"));
            }
        }
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
