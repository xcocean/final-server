package top.lingkang.finalserver.server.web.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.cookie.Cookie;
import top.lingkang.finalserver.server.core.FinalServerConfiguration;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author lingkang
 * Created by 2022/12/6
 * @since 1.0.0
 */
public class HttpResponse implements Response {
    private ChannelHandlerContext ctx;
    private HttpHeaders headers = FinalServerConfiguration.defaultResponseHeaders.get();

    private boolean isReady;
    private boolean isStatic;
    private String filePath;
    private byte[] content;
    private int code;
    private Set<Cookie> cookies = new TreeSet<>();

    public HttpResponse(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void setHeader(String name, String value) {
        headers.set(name, value);
    }

    @Override
    public void returnString(String obj) {
        checkReady();
        if (obj != null) {
            content = obj.getBytes(StandardCharsets.UTF_8);
            if (!headers.contains(HttpHeaderNames.CONTENT_TYPE))
                headers.set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN);
        }
        isReady = true;
    }

    @Override
    public void returnJsonObject(Object json) {
        checkReady();
        if (json != null) {
            content = FinalServerConfiguration.serializable.jsonTo(json);
            if (!headers.contains(HttpHeaderNames.CONTENT_TYPE))
                headers.set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
        }
        isReady = true;
    }

    @Override
    public void returnTemplate(String template) {
        returnTemplate(template, null);
    }

    @Override
    public void returnTemplate(String template, Map<String, Object> map) {
        checkReady();
        try {
            content = TemplateUtils.getTemplate(template, map);
            if (!headers.contains(HttpHeaderNames.CONTENT_TYPE))
                headers.set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_HTML);
        } catch (Exception e) {
            throw new RuntimeException("解析模板异常：", e);
        }
        isReady = true;
    }

    @Override
    public void returnFile(String filePath) {
        checkReady();
        isReady = true;
        isStatic = true;
        this.filePath = filePath;
    }

    @Override
    public void setStatusCode(int code) {
        this.code = code;
    }

    @Override
    public boolean isReady() {
        return isReady;
    }

    @Override
    public void addCookie(Cookie cookie) {
        cookies.add(cookie);
    }

    @Override
    public Set<Cookie> getCookies() {
        return cookies;
    }

    private void checkReady() {
        if (isReady) {
            throw new RuntimeException("007-已经设置返回值，不能重复设置");
        }
    }


    // get

    public String getFilePath() {
        return filePath;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public void setStatic(boolean aStatic) {
        isStatic = aStatic;
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public byte[] getContent() {
        return content;
    }

    public int getCode() {
        return code;
    }
}
