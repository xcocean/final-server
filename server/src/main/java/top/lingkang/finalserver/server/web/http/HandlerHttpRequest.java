package top.lingkang.finalserver.server.web.http;


import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import top.lingkang.finalserver.server.annotation.NotNull;
import top.lingkang.finalserver.server.web.FinalServerWeb;
import top.lingkang.finalserver.server.web.handler.ControllerHandlerChain;
import top.lingkang.finalserver.server.web.handler.FilterHandlerChain;


/**
 * @author lingkang
 * Created by 2022/12/6
 */
public class HandlerHttpRequest extends SimpleChannelInboundHandler<FinalServerContext> {

    private Filter[] filter;
    private ControllerHandlerChain controller;

    public HandlerHttpRequest(Filter[] filter, ControllerHandlerChain controller) {
        this.filter = filter;
        this.controller = controller;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FinalServerContext context) throws Exception {
        System.out.println(context.getRequest().getHttpMethod().name() + "  path=" + context.getRequest().getPath());

        filter[0].doFilter(context,filter[1].doFilter(context,filter[2].doFilter(context,controller.doFilter(context))));

        if (context.getResponse().isReady()) {
            sendString(ctx, (HttpResponse) context.getResponse(), 200);
        } else {// 返回空值
            sendString(ctx, "", 200);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        sendString(ctx, "后台异常", 500);
        cause.printStackTrace();
    }

    private void sendString(ChannelHandlerContext ctx, @NotNull String context, int status) {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, HttpResponseStatus.valueOf(status),
                Unpooled.copiedBuffer(context, CharsetUtil.UTF_8)
        );
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, context.getBytes().length);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private void sendString(ChannelHandlerContext ctx, HttpResponse httpResponse, int status) {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, HttpResponseStatus.valueOf(status),
                Unpooled.copiedBuffer(httpResponse.getContent(), CharsetUtil.UTF_8)
        );
        response.headers().set(httpResponse.getHeaders());
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, httpResponse.getContent().getBytes().length);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

}
