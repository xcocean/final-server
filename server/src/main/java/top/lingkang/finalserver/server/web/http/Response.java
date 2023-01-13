package top.lingkang.finalserver.server.web.http;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.cookie.Cookie;
import top.lingkang.finalserver.server.web.entity.ResponseFile;

import java.util.Map;
import java.util.Set;

/**
 * @author lingkang
 * Created by 2022/12/6
 * @since 1.0.0
 * return 时，并非直接返回内容结果，而是先做标记。在请求断开前才会将return的内容写到客户端
 */
public interface Response {
    // 设置请求头
    void setHeader(String name, String value);

    HttpHeaders getHeaders();

    void returnString(String str);

    void returnJsonObject(Object json);

    void returnTemplate(String template);

    void returnTemplate(String templatePath, Map<String, Object> map);

    // 返回文件，前端会下载文件。filePath为文件所在路径
    void returnFile(ResponseFile responseFile);

    void returnBytes(byte[] bytes);

    /**
     * 请求转发，使用http再次请求。存在局限性：
     * 例如不能转发文件下载、大数据内容（MB级别数据）
     */
    void returnForward(String forwardPath);

    // 重定向
    void returnRedirect(String url);

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

    Map<String, Object> getTemplateMap();

    ResponseFile getResponseFile();

    int getStatusCode();

    byte[] getContent();

    String getForwardPath();
}
