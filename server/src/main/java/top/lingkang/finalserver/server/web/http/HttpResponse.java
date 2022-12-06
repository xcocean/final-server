package top.lingkang.finalserver.server.web.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

import java.io.InputStream;

/**
 * @author lingkang
 * Created by 2022/12/6
 */
public class HttpResponse implements Response{
    private ChannelHandlerContext ctx;
    private FullHttpRequest msg;

    public HttpResponse(ChannelHandlerContext ctx, FullHttpRequest msg) {
        this.ctx = ctx;
        this.msg = msg;
    }

    @Override
    public void setHeader(String name, String value) {

    }

    @Override
    public InputStream getInputStream() {
        return null;
    }

    @Override
    public void setStatusCode(int code) {

    }
}
