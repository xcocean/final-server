package top.lingkang.finalserver.server.web.http;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.cookie.Cookie;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author lingkang
 * Created by 2022/12/6
 * @since 1.0.0
 */
public interface Request {
    /**
     * 获取请求ID，指向netty的长ID
     */
    String requestId();

    /**
     * 获取请求路径
     */
    String getPath();

    /**
     * 获取请求参数
     */
    String getParam(String name);

    /**
     * 获取请求体，非GET请求才有值
     * 适用于post内容中 raw
     */
    String getBody();

    /**
     * 获取参数转化为bean对象
     */
    <T> T getParamToBean(Class<T> beanClass);

    /**
     * 获取所有参数
     */
    Map<String,String> getParams();

    /**
     * 获取上传的文件列表
     */
    List<MultipartFile> getFileUpload();

    /**
     * 从请求头中获取
     */
    String getHeader(String name);

    /**
     * 获取请求头
     */
    HttpHeaders getHeaders();

    /**
     * 获取请求IP
     */
    String getIp();

    /**
     * 获取请求主机
     */
    int getPort();

    /**
     * 获取请求方法
     */
    HttpMethod getHttpMethod();

    /**
     * 获取指定cookie值
     */
    Cookie getCookie(String name);

    /**
     * 获取cookies
     */
    Set<Cookie> getCookies();

    /**
     * 获取当前会话
     */
    Session getSession();

    /**
     * 释放此请求，final-server会在最后执行
     */
    void release();

    /**
     * 获取请求完整信息
     */
    FullHttpRequest getFullHttpRequest();
}
