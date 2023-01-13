package top.lingkang.finalserver.server.web.http;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.multipart.FileUpload;

import java.util.List;
import java.util.Map;
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

    Map<String,String> getParams();

    List<MultipartFile> getFileUpload();

    String getHeader(String name);

    HttpHeaders getHeaders();

    String getIp();

    int getPort();

    HttpMethod getHttpMethod();

    Cookie getCookie(String name);

    Set<Cookie> getCookies();

    Session getSession();

    void release();

    FullHttpRequest getFullHttpRequest();
}
