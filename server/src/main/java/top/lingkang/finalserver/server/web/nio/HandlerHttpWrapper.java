package top.lingkang.finalserver.server.web.nio;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import top.lingkang.finalserver.server.constant.FinalServerConstants;
import top.lingkang.finalserver.server.web.http.FinalServerContext;
import top.lingkang.finalserver.server.web.http.HttpRequest;
import top.lingkang.finalserver.server.web.http.HttpResponse;

import java.net.URLDecoder;

/**
 * @author lingkang
 * Created by 2022/12/6
 * @since 1.0.0
 */
public class HandlerHttpWrapper extends SimpleChannelInboundHandler<FullHttpRequest> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        // 提前将url解码，防止中文乱码
        msg.setUri(URLDecoder.decode(msg.uri(), "UTF-8"));
        msg.setUri(URLDecoder.decode(msg.uri(), FinalServerConstants.encoding));

        // 构建上下文
        FinalServerContext context = new FinalServerContext(ctx);
        context.setRequest(new HttpRequest(ctx, msg));
        context.setResponse(new HttpResponse(ctx));

        // 初始化上下文中的请求与响应
        FinalServerHttpContext.init(context.getRequest(), context.getResponse());

        // next
        ctx.fireChannelRead(context);
    }
}
