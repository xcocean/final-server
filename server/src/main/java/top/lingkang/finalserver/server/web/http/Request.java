package top.lingkang.finalserver.server.web.http;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.cookie.Cookie;

import java.util.Set;

/**
 * @author lingkang
 * Created by 2022/12/6
 * @since 1.0.0
 */
public interface Request {
    String requestId();

    String getPath();

    String getParam(String name);

    String getHeader(String name);

    HttpHeaders getHeaders();

    String getIp();

    int getPort();

    HttpMethod getHttpMethod();

    Set<Cookie> getCookies();
}
