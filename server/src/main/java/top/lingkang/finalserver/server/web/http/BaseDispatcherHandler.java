package top.lingkang.finalserver.server.web.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.lingkang.finalserver.server.constant.FinalServerConstants;

import java.net.URLDecoder;

/**
 * @author lingkang
 * Created by 2022/12/6
 * @since 1.0.0
 * 调度处理
 */
public class BaseDispatcherHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private static final Logger log = LoggerFactory.getLogger(BaseDispatcherHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        // 提前将url解码，防止中文乱码
        msg.setUri(URLDecoder.decode(msg.uri(), FinalServerConstants.encoding));

        // http
        httpHandler(ctx, msg);
    }

    protected void httpHandler(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        // 构建上下文
        FinalServerContext context = new FinalServerContext(new HttpRequest(ctx, msg));
        context.setResponse(new HttpResponse(ctx));

        // 写内容
        ctx.pipeline().addLast(new ChunkedWriteHandler());

        // http 处理
        ctx.pipeline().addLast(new HandlerHttpRequest());

        // next
        ctx.fireChannelRead(context);
    }
}
