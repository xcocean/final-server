package top.lingkang.finalserver.server.core.impl;

import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import top.lingkang.finalserver.server.constant.FinalServerConstants;

/**
 * @author lingkang
 * Created by 2022/12/8
 * @since 1.0.0
 */
public class FinalServerDefaultHttpHeaders {

    public HttpHeaders get() {
        return new DefaultHttpHeaders()
                .remove(HttpHeaderNames.CONTENT_TYPE)
                .set(HttpHeaderNames.CONTENT_ENCODING, FinalServerConstants.encoding)
                .set("Server", FinalServerConstants.version);
    }
}
