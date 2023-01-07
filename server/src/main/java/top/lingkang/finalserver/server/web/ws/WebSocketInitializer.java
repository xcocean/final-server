package top.lingkang.finalserver.server.web.ws;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

/**
 * @author lingkang
 * Created by 2022/12/11
 * @since 1.0.0
 */
public class WebSocketInitializer extends SimpleChannelInboundHandler<WebSocketFrame> {
    private WebSocketHandler handler;
    private HttpHeaders headers;
    private WebSocketSession session;
    private boolean isFirst;

    public WebSocketInitializer(WebSocketHandler handler, HttpHeaders headers) {
        this.handler = handler;
        this.headers = headers;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
        if (isFirst)
            return;
        isFirst = true;
        session = new WebSocketSession(ctx, headers);
        WebSocketDispatchManage.addConnect(session);
        handler.onOpen(session);
        WebSocketDispatchManage.sessionMap.put(session.getId(), session);
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame msg) throws Exception {
        handler.onMessage(session, new Message(msg));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        WebSocketDispatchManage.exceptionConnect(session, cause);
        handler.onException(session, cause);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        WebSocketDispatchManage.sessionMap.remove(session.getId());
        WebSocketDispatchManage.removeConnect(session);
        handler.onClose(session);
        if (ctx.channel().isActive())
            ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }
}
