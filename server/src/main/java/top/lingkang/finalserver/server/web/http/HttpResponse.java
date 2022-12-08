package top.lingkang.finalserver.server.web.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import top.lingkang.finalserver.server.core.FinalServerConfiguration;

import java.util.Map;

/**
 * @author lingkang
 * Created by 2022/12/6
 */
public class HttpResponse implements Response {
    private ChannelHandlerContext ctx;
    private FullHttpRequest msg;
    private HttpHeaders headers = FinalServerConfiguration.defaultResponseHeaders;

    private boolean isReady;
    private boolean isStatic;
    private String content, filePath;
    private int code;

    public HttpResponse(ChannelHandlerContext ctx, FullHttpRequest msg) {
        this.ctx = ctx;
        this.msg = msg;
    }

    @Override
    public void setHeader(String name, String value) {

    }

    @Override
    public void returnString(String obj) {
        content = obj;
        isReady = true;
        headers.set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN + "; charset=utf-8");
    }

    @Override
    public void returnJsonObject(Object json) {
        content = json.toString();
        headers.set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON + "; charset=utf-8");
    }

    @Override
    public void returnTemplate(String template) {
        content = template;
        headers.set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_HTML + "; charset=utf-8");
    }

    @Override
    public void returnTemplate(String template, Map<String, Object> map) {
        headers.set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_HTML + "; charset=utf-8");
    }

    @Override
    public void returnFile(String filePath) {
        returnFile(filePath, null);

    }

    @Override
    public void returnFile(String filePath, HttpHeaders headers) {
        isReady = true;
        isStatic = true;
        this.filePath = filePath;
        if (headers != null)
            this.headers.add(headers);
    }

    @Override
    public void setStatusCode(int code) {
        this.code = code;
    }

    @Override
    public boolean isReady() {
        return isReady;
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

    public FullHttpRequest getMsg() {
        return msg;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public String getContent() {
        return content;
    }

    public int getCode() {
        return code;
    }
}
