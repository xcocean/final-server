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

    // 获取响应头
    HttpHeaders getHeaders();

    // 返回string结果，http/text
    void returnString(String str);

    // 返回json结果
    void returnJsonObject(Object json);

    // 返回模板解析
    void returnTemplate(String template);

    // 返回模板解析和解析map
    void returnTemplate(String templatePath, Map<String, Object> map);

    // 返回文件，前端会下载文件。filePath为文件所在路径
    void returnFile(ResponseFile responseFile);

    // 返回字节
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

    // 获取模板解析的路径
    String getTemplatePath();

    // 获取模板解析的map映射
    Map<String, Object> getTemplateMap();

    // 获取响应文件
    ResponseFile getResponseFile();

    // 获取响应状态码
    int getStatusCode();

    // 获取响应的字节内容
    byte[] getContent();

    // 获取重定向路径
    String getForwardPath();
}
