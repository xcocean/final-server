package top.lingkang.finalserver.server.web.nio;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import top.lingkang.finalserver.server.constant.FinalServerConstants;
import top.lingkang.finalserver.server.core.HttpParseTemplate;
import top.lingkang.finalserver.server.web.FinalServerHttpContext;
import top.lingkang.finalserver.server.web.http.*;

import java.net.URLDecoder;

/**
 * @author lingkang
 * Created by 2022/12/6
 * @since 1.0.0
 */
class HandlerHttpWrapper extends SimpleChannelInboundHandler<FullHttpRequest> {
    private FilterChain filterChain;
    private HttpParseTemplate parseTemplate;

    public HandlerHttpWrapper(FilterChain filterChain, HttpParseTemplate parseTemplate) {
        this.filterChain = filterChain;
        this.parseTemplate = parseTemplate;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        /*if ("websocket".equals(msg.headers().get("Upgrade"))){// 将url设置成一致的
            System.out.println(msg.uri());
            int index = msg.uri().indexOf("?");
            if (index!=-1)
            msg.setUri("/ws");
            msg.setUri("/ws");
            ctx.pipeline().addLast(new WebSocketServerCompressionHandler());
            ctx.pipeline().addLast(new WebSocketServerProtocolHandler(
                    "/ws", //路径
                    null,
                    true,
                    65536, //最大处理数据内容
                    false,  //掩码加密
                    true //允许 websocketPath 路径匹配，否则走全匹配，例如 websocketPath=/ws request=/ws?user=zhangsan 将匹配不上，无法处理
            ));
            //websocket 处理
            ctx.pipeline().addLast(new WebsocketServer());

            ctx.fireChannelRead(msg.retain());
            return;
        }*/

        // http
        httpHandler(ctx,msg);
    }

    private void httpHandler(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception{
        // 提前将url解码，防止中文乱码
        msg.setUri(URLDecoder.decode(msg.uri(), FinalServerConstants.encoding));

        // 构建上下文
        FinalServerContext context = new FinalServerContext(ctx);
        context.setRequest(new HttpRequest(ctx, msg));
        context.setResponse(new HttpResponse(ctx));

        // 初始化上下文中的请求与响应
        FinalServerHttpContext.init(context.getRequest(), context.getResponse());

        // http 处理
        ctx.pipeline().addLast(new HandlerHttpRequest(filterChain, parseTemplate));
        // next
        ctx.fireChannelRead(context);
    }
}
