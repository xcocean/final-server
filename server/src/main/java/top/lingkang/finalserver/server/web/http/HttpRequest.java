package top.lingkang.finalserver.server.web.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;

import java.net.InetSocketAddress;

/**
 * @author lingkang
 * Created by 2022/12/6
 */
public class HttpRequest implements Request {
    private ChannelHandlerContext ctx;
    private FullHttpRequest msg;

    public HttpRequest(ChannelHandlerContext ctx, FullHttpRequest msg) {
        this.ctx = ctx;
        this.msg = msg;
    }

    @Override
    public String id() {
        return ctx.channel().id().asLongText();
    }

    @Override
    public String getPath() {
        return msg.uri();
    }

    @Override
    public String getParam(String name) {
        return null;
    }

    @Override
    public String getHeader(String name) {
        return msg.headers().get(name);
    }

    @Override
    public HttpHeaders getHeaders() {
        return msg.headers();
    }

    @Override
    public String getIp() {
        return ((InetSocketAddress) ctx.channel().remoteAddress()).getHostString();
    }

    @Override
    public int getPort() {
        return ((InetSocketAddress) ctx.channel().remoteAddress()).getPort();
    }

    @Override
    public HttpMethod getHttpMethod() {
        return msg.method();
    }
}
