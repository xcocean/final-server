package top.lingkang.finalserver.server.web.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.lingkang.finalserver.server.constant.FinalServerConstants;
import top.lingkang.finalserver.server.core.FinalServerProperties;
import top.lingkang.finalserver.server.utils.BeanUtils;
import top.lingkang.finalserver.server.utils.CommonUtils;
import top.lingkang.finalserver.server.utils.HttpUtils;
import top.lingkang.finalserver.server.web.nio.ws.FinalWebSocketServerProtocolHandler;
import top.lingkang.finalserver.server.web.nio.ws.WebSocketHandler;
import top.lingkang.finalserver.server.web.nio.ws.WebSocketInitializer;
import top.lingkang.finalserver.server.web.nio.ws.WebSocketManage;

import java.net.URLDecoder;

/**
 * @author lingkang
 * Created by 2022/12/6
 * @since 1.0.0
 */
public class HandlerHttpWrapper extends SimpleChannelInboundHandler<FullHttpRequest> {
    private static final Logger log = LoggerFactory.getLogger(HandlerHttpWrapper.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        // 提前将url解码，防止中文乱码
        msg.setUri(URLDecoder.decode(msg.uri(), FinalServerConstants.encoding));

        // websocket处理
        if ("websocket".equals(msg.headers().get("Upgrade"))) {
            webSocketHandler(ctx, msg);
            return;
        }

        // http
        httpHandler(ctx, msg);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);
        FinalServerContext.removeCurrentContext();

        // 监听：之后
        CommonUtils.pushWebListenerAfter();
    }

    private void httpHandler(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        // 构建上下文
        FinalServerContext context = new FinalServerContext(ctx);
        context.setRequest(new HttpRequest(ctx, msg));
        context.setResponse(new HttpResponse(ctx));

        // 写内容
        ctx.pipeline().addLast(new ChunkedWriteHandler());
        // http 处理
        ctx.pipeline().addLast(new HandlerHttpRequest());

        // next
        ctx.fireChannelRead(context);
    }

    private void webSocketHandler(ChannelHandlerContext ctx, FullHttpRequest request) {
        int index = request.uri().indexOf("?");
        String path;
        if (index != -1)
            path = request.uri().substring(0, index);
        else
            path = request.uri();
        WebSocketHandler handler = BeanUtils.getBean(WebSocketManage.class).getHandler(path);
        if (handler == null) {
            log.warn("未找到websocket处理, 它将被直接关闭连接. ws={}", path);
            HttpUtils.closeHttpWebsocket(ctx, "404");
            return;
        }

        // 开始握手连接
        ctx.pipeline().addLast(new WebSocketServerCompressionHandler());
        ctx.pipeline().addLast(new FinalWebSocketServerProtocolHandler(
                request.uri(), //路径
                null,
                true,
                FinalServerProperties.websocket_maxMessage, //最大处理数据内容
                false,  //掩码加密
                true //允许 websocketPath 路径匹配，否则走全匹配，例如 websocketPath=/ws request=/ws?user=zhangsan 将匹配不上，无法处理
        ));

        //websocket 处理
        ctx.pipeline().addLast(new WebSocketInitializer(handler, request.headers()));

        // 后续处理
        ctx.fireChannelRead(request.retain());
    }
}
