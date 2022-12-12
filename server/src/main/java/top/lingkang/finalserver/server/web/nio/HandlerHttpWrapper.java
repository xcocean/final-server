package top.lingkang.finalserver.server.web.nio;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.lingkang.finalserver.server.constant.FinalServerConstants;
import top.lingkang.finalserver.server.core.HttpParseTemplate;
import top.lingkang.finalserver.server.utils.BeanUtils;
import top.lingkang.finalserver.server.utils.HttpUtils;
import top.lingkang.finalserver.server.web.FinalServerHttpContext;
import top.lingkang.finalserver.server.web.http.*;
import top.lingkang.finalserver.server.web.nio.ws.WebSocketManage;

import java.net.URLDecoder;

/**
 * @author lingkang
 * Created by 2022/12/6
 * @since 1.0.0
 */
class HandlerHttpWrapper extends SimpleChannelInboundHandler<FullHttpRequest> {
    private static final Logger log = LoggerFactory.getLogger(HandlerHttpWrapper.class);
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
        // 提前将url解码，防止中文乱码
        msg.setUri(URLDecoder.decode(msg.uri(), FinalServerConstants.encoding));

        if ("websocket".equals(msg.headers().get("Upgrade"))) {
            webSocketHandler(ctx, msg);
            return;
        }
        // http
        httpHandler(ctx, msg);
    }

    private void httpHandler(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
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

    private void webSocketHandler(ChannelHandlerContext ctx, FullHttpRequest msg) {
        WebSocketManage manage = BeanUtils.getBean(WebSocketManage.class);
        try {
            boolean isHandler = manage.getFilterChain().doFilter(ctx, msg);
            if (!isHandler) {
                log.warn("WebSocket过滤器未对请求/握手放行，将自动关闭此次握手连接。应调用 filterChain.doFilter(ctx, msg); 做后续处理。 path={}", msg.uri());
                if (ctx.channel().isActive())
                    HttpUtils.sendString(ctx, "", 404);
            }
        } catch (Exception e) {
            if (ctx.channel().isActive())
                ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
            e.printStackTrace();
        }

        /*
        WebSocketHandler handler = manage.getHandler(path);
        if (handler == null) {
            log.warn("未找到websocket处理, 它将被直接关闭连接. path={}", path);
            HttpUtils.closeHttpWebsocket(ctx, "404");
            return;
        }

        // 开始握手连接
        ctx.pipeline().addLast(new WebSocketServerCompressionHandler());
        ctx.pipeline().addLast(new FinalWebSocketServerProtocolHandler(
                msg.uri(), //路径
                null,
                true,
                FinalServerProperties.websocket_maxMessage, //最大处理数据内容
                false,  //掩码加密
                true //允许 websocketPath 路径匹配，否则走全匹配，例如 websocketPath=/ws request=/ws?user=zhangsan 将匹配不上，无法处理
        ));

        //websocket 处理
        ctx.pipeline().addLast(new WebSocketInitializer(handler, msg.headers()));

        ctx.fireChannelRead(msg.retain());*/
    }
}
