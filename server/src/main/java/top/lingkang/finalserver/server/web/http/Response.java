package top.lingkang.finalserver.server.web.http;

import io.netty.handler.codec.http.HttpHeaders;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * @author lingkang
 * Created by 2022/12/6
 * @since 1.0.0
 */
public interface Response {
    void setHeader(String name,String value);

    void returnString(String obj);

    void returnJsonObject(Object json);

    void returnTemplate(String template);

    void returnTemplate(String template, Map<String, Object> map);

    void returnFile(String filePath);

    void setStatusCode(int code);

    boolean isReady();

}
