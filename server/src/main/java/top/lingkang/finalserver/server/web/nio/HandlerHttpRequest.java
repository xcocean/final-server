package top.lingkang.finalserver.server.web.nio;


import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import top.lingkang.finalserver.server.web.FinalServerWeb;
import top.lingkang.finalserver.server.web.http.FinalServerContext;
import top.lingkang.finalserver.server.web.http.HandlerChain;


/**
 * @author lingkang
 * Created by 2022/12/6
 */
public class HandlerHttpRequest extends SimpleChannelInboundHandler<FinalServerContext> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FinalServerContext context) throws Exception {
        try {
            ChannelId id = ctx.channel().id();
            System.out.println(context.getRequest().getHttpMethod().name());
            System.out.println(id.asLongText());

            for (HandlerChain chain : FinalServerWeb.handlerChain) {
                if (!chain.handler(context))
                    break;
            }
            HttpMethod get = HttpMethod.GET;
            System.out.println(ctx.channel().bytesBeforeWritable());
            if (!context.getResponse().isWrite())
                send(ctx, "404", HttpResponseStatus.NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ctx.channel().flush();
        send(ctx, null, HttpResponseStatus.OK);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        send(ctx, "后台异常", HttpResponseStatus.INTERNAL_SERVER_ERROR);
        cause.printStackTrace();
    }

    private void send(ChannelHandlerContext ctx, String context, HttpResponseStatus status) {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, status,
                Unpooled.copiedBuffer(context, CharsetUtil.UTF_8)
        );
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain;charset=UTF-8");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

}
