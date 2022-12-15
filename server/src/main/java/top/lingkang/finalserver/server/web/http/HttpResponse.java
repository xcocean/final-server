package top.lingkang.finalserver.server.web.http;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.cookie.Cookie;
import top.lingkang.finalserver.server.core.FinalServerConfiguration;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
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
    private boolean isTemplate;
    private String templatePath;
    private HashMap<String, Object> map;
    private String filePath;
    private byte[] content;
    private int code = 200;
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
                headers.set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
        }
        isReady = true;
    }

    @Override
    public void returnJsonObject(Object json) {
        checkReady();
        if (json != null) {
            content = FinalServerConfiguration.serializable.jsonTo(json);
            if (!headers.contains(HttpHeaderNames.CONTENT_TYPE))
                headers.set(HttpHeaderNames.CONTENT_TYPE, "application/json; charset=UTF-8");
        }
        isReady = true;
    }

    @Override
    public void returnTemplate(String template) {
        returnTemplate(template, null);
    }

    @Override
    public void returnTemplate(String templatePath, HashMap<String, Object> map) {
        Assert.notEmpty(templatePath, "templatePath 模板不能为空");
        checkReady();
        this.templatePath = templatePath;
        this.map = map;
        isTemplate = true;
        isReady = true;
        if (!headers.contains(HttpHeaderNames.CONTENT_TYPE))
            headers.set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
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
    public boolean isTemplate() {
        return isTemplate;
    }

    @Override
    public void addCookie(Cookie cookie) {
        for (Cookie c : cookies)
            if (c.name().equals(cookie.name())) {
                cookies.remove(c);
                break;
            }
        cookies.add(cookie);
    }

    @Override
    public Set<Cookie> getCookies() {
        return cookies;
    }

    @Override
    public String getTemplatePath() {
        return templatePath;
    }

    @Override
    public HashMap<String, Object> getTemplateMap() {
        return map;
    }

    @Override
    public void sendRedirect(String url) {
        code = 302;
        if (StrUtil.isBlank(url))
            url = "/";
        headers.set(HttpHeaderNames.LOCATION, url);
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
