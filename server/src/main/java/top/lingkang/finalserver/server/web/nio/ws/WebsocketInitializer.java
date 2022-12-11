package top.lingkang.finalserver.server.web.nio.ws;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

/**
 * @author lingkang
 * Created by 2022/12/11
 */
public class WebsocketInitializer extends SimpleChannelInboundHandler <WebSocketFrame> {
    public WebsocketInitializer() {
        System.out.println(111);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
        System.out.println("channelRead");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame msg) throws Exception {
        TextWebSocketFrame frame= (TextWebSocketFrame) msg;
        System.out.println(frame.text());
        System.out.println("channelRead0");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        System.out.println("exceptionCaught");
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);
        System.out.println("handlerRemoved");
    }
}
