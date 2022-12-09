package top.lingkang.finalserver.server.web.nio;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import top.lingkang.finalserver.server.web.http.FinalServerContext;
import top.lingkang.finalserver.server.web.http.HttpRequest;
import top.lingkang.finalserver.server.web.http.HttpResponse;

/**
 * @author lingkang
 * Created by 2022/12/6
 * @since 1.0.0
 */
public class HandlerHttpWrapper extends SimpleChannelInboundHandler<FullHttpRequest> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        FinalServerContext context = new FinalServerContext(ctx);
        context.setRequest(new HttpRequest(ctx, msg));
        context.setResponse(new HttpResponse(ctx));
        ctx.fireChannelRead(context);
    }
}
