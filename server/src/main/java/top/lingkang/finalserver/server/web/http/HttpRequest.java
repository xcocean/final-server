package top.lingkang.finalserver.server.web.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.util.List;

/**
 * @author lingkang
 * Created by 2022/12/6
 * @since 1.0.0
 */
public class HttpRequest implements Request {
    private ChannelHandlerContext ctx;
    private FullHttpRequest msg;
    private QueryStringDecoder queryUri;
    private HttpPostRequestDecoder queryBody;

    public HttpRequest(ChannelHandlerContext ctx, FullHttpRequest msg) {
        this.ctx = ctx;
        this.msg = msg;
        queryUri = new QueryStringDecoder(msg.uri());
        queryBody = new HttpPostRequestDecoder(msg);
    }

    @Override
    public String id() {
        return ctx.channel().id().asLongText();
    }

    @Override
    public String getPath() {
        try {
            return URLDecoder.decode(queryUri.path(), "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getParam(String name) {
        List<String> put = queryUri.parameters().get(name);
        if (msg.method() == HttpMethod.GET) {
            if (put == null)
                return null;
            return put.get(0);
        }
        if (put != null)
            return put.get(0);
        InterfaceHttpData data = queryBody.getBodyHttpData(name);
        if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute && data != null) {
            try {
                return ((Attribute) data).getValue();
            } catch (IOException e) {
            }
        }
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
