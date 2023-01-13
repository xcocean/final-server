package top.lingkang.finalserver.server.web.http;

import cn.hutool.core.util.StrUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.cookie.Cookie;
import top.lingkang.finalserver.server.core.FinalServerConfiguration;
import top.lingkang.finalserver.server.error.FinalServerException;
import top.lingkang.finalserver.server.web.entity.ResponseFile;

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
    private HttpHeaders headers = FinalServerConfiguration.defaultResponseHeaders.get(true);

    private boolean isReady;
    private ResponseFile responseFile;
    private boolean isTemplate;
    private String templatePath;
    private Map<String, Object> map;
    private byte[] content = new byte[0];
    private int code = 200;
    private Set<Cookie> cookies = new TreeSet<>();
    private String forwardPath;

    public HttpResponse(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void setHeader(String name, String value) {
        headers.set(name, value);
    }

    @Override
    public void returnString(String str) {
        checkReady();
        if (str != null) {
            content = str.getBytes(StandardCharsets.UTF_8);
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
    public void returnTemplate(String template, Map<String, Object> map) {
        if (StrUtil.isEmpty(template))
            new FinalServerException("templatePath 模板不能为空");
        checkReady();
        this.templatePath = template;
        this.map = map;
        isTemplate = true;
        isReady = true;
        if (!headers.contains(HttpHeaderNames.CONTENT_TYPE))
            headers.set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
    }

    @Override
    public void returnFile(ResponseFile responseFile) {
        checkReady();
        isReady = true;
        if (responseFile.getFilePath() == null)
            throw new FinalServerException("返回文件路径不能为空");
        this.responseFile = responseFile;
    }

    @Override
    public void returnForward(String forwardPath) {
        checkReady();
        isReady = true;
        this.forwardPath = forwardPath;
    }

    @Override
    public void returnBytes(byte[] bytes) {
        checkReady();
        isReady = true;
        this.content = bytes;
    }

    @Override
    public void returnRedirect(String url) {
        checkReady();
        isReady = true;
        code = 302;
        if (StrUtil.isBlank(url))
            url = "/";
        headers.set(HttpHeaderNames.LOCATION, url);
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
    public Map<String, Object> getTemplateMap() {
        return map;
    }

    @Override
    public ResponseFile getResponseFile() {
        return responseFile;
    }

    @Override
    public int getStatusCode() {
        return code;
    }

    @Override
    public String getForwardPath() {
        return forwardPath;
    }

    private void checkReady() {
        if (isReady) {
            throw new FinalServerException("007-已经设置返回值，不能重复设置");
        }
    }


    // get
    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public byte[] getContent() {
        return content;
    }
}
