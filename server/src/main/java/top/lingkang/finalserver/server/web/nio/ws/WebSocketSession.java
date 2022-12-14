package top.lingkang.finalserver.server.web.nio.ws;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

/**
 * @author lingkang
 * Created by 2022/12/12
 */
public class WebSocketSession {
    private ChannelHandlerContext context;
    private HttpHeaders headers;

    public WebSocketSession(ChannelHandlerContext context, HttpHeaders headers) {
        this.context = context;
        this.headers = headers;
    }

    public String getId() {
        return context.channel().id().asLongText();
    }

    public void write(String content) {
        context.writeAndFlush(new TextWebSocketFrame(content));
    }

    public void write(byte[] bytes) {
        context.writeAndFlush(new BinaryWebSocketFrame(Unpooled.wrappedBuffer(bytes)));
    }

    public void write(Object obj) {
        write(obj.toString());
    }

    public void close() {
        if (context.channel().isActive()) {
            context.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
            context.fireChannelInactive();
        }
    }

    public ChannelHandlerContext getContext() {
        return context;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }
}
