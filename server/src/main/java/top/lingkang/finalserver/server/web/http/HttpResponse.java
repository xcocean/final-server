package top.lingkang.finalserver.server.web.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

/**
 * @author lingkang
 * Created by 2022/12/6
 */
public class HttpResponse implements Response {
    private ChannelHandlerContext ctx;
    private FullHttpRequest msg;

    private boolean isWrite;

    public HttpResponse(ChannelHandlerContext ctx, FullHttpRequest msg) {
        this.ctx = ctx;
        this.msg = msg;
    }

    @Override
    public void setHeader(String name, String value) {

    }

    @Override
    public void writeString(Object obj) {
        isWrite = true;
        ctx.channel().write(obj);
    }

    @Override
    public void setStatusCode(int code) {

    }

    @Override
    public boolean isWrite() {
        return isWrite;
    }
}
