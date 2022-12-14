package top.lingkang.finalserver.server.web.http;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.cookie.Cookie;

import java.util.HashMap;
import java.util.Set;

/**
 * @author lingkang
 * Created by 2022/12/6
 * @since 1.0.0
 */
public interface Response {
    // 设置请求头
    void setHeader(String name, String value);

    HttpHeaders getHeaders();

    void returnString(String obj);

    void returnJsonObject(Object json);

    void returnTemplate(String template);

    void returnTemplate(String templatePath, HashMap<String, Object> map);

    void returnFile(String filePath);

    // 设置响应http的状态码，默认 200
    void setStatusCode(int code);

    // 是否已经准备好响应，不能重复return
    boolean isReady();

    boolean isTemplate();

    // 对当前会话响应添加cookie
    void addCookie(Cookie cookie);

    // 只能获取到本次会话响应添加的cookie
    Set<Cookie> getCookies();

    String getTemplatePath();

    HashMap<String, Object> getTemplateMap();

    // 重定向
    void sendRedirect(String url);

}
