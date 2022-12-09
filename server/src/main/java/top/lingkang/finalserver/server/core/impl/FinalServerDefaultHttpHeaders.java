package top.lingkang.finalserver.server.core.impl;

import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import top.lingkang.finalserver.server.constant.FinalServerConstants;

/**
 * @author lingkang
 * Created by 2022/12/8
 * @since 1.0.0
 */
public class FinalServerDefaultHttpHeaders extends DefaultHttpHeaders {
    public FinalServerDefaultHttpHeaders() {
        set(HttpHeaderNames.CONTENT_ENCODING, "utf-8");
        set("Server", "Final Server " + FinalServerConstants.version);
    }
}
