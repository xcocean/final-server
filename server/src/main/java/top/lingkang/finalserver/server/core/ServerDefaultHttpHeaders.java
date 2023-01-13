package top.lingkang.finalserver.server.core;

import io.netty.handler.codec.http.HttpHeaders;

/**
 * @author lingkang
 * 2023/1/10
 * @since 1.0.0
 **/
public interface ServerDefaultHttpHeaders {

    HttpHeaders get(boolean isNew);
}
