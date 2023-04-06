package top.lingkang.finalserver.server.core.impl;

import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import top.lingkang.finalserver.server.core.ServerDefaultHttpHeaders;

import java.util.Date;

/**
 * @author lingkang
 * Created by 2022/12/8
 * @since 1.0.0
 */
public class FinalServerDefaultHttpHeaders implements ServerDefaultHttpHeaders {

    public HttpHeaders get() {
        return new DefaultHttpHeaders()
                .remove(HttpHeaderNames.CONTENT_TYPE)
                .set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE)
                .set("keep-alive", "timeout=30")
                .set(HttpHeaderNames.DATE, new Date());
    }
}
