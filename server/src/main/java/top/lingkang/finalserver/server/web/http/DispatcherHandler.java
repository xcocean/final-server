package top.lingkang.finalserver.server.web.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.lingkang.finalserver.server.constant.FinalServerConstants;
import top.lingkang.finalserver.server.core.FinalServerConfiguration;
import top.lingkang.finalserver.server.utils.BeanUtils;
import top.lingkang.finalserver.server.web.ws.FinalWebSocketServerProtocolHandler;
import top.lingkang.finalserver.server.web.ws.WebSocketDispatch;
import top.lingkang.finalserver.server.web.ws.WebSocketHandler;
import top.lingkang.finalserver.server.web.ws.WebSocketInitializer;

import java.net.URLDecoder;

/**
 * @author lingkang
 * Created by 2022/12/6
 * @since 1.0.0
 * 调度处理
 */
public class DispatcherHandler extends BaseDispatcherHandler {
    private static final Logger log = LoggerFactory.getLogger(DispatcherHandler.class);

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

    private void webSocketHandler(ChannelHandlerContext ctx, FullHttpRequest request) {
        int index = request.uri().indexOf("?");
        String path;
        if (index != -1)
            path = request.uri().substring(0, index);
        else
            path = request.uri();
        WebSocketHandler handler = BeanUtils.getBean(WebSocketDispatch.class).getHandler(path);
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
                FinalServerConfiguration.websocketMaxMessage, //最大处理数据内容
                false,  //掩码加密
                true //允许 websocketPath 路径匹配，否则走全匹配，例如 websocketPath=/ws request=/ws?user=zhangsan 将匹配不上，无法处理
        ));

        //websocket 处理
        ctx.pipeline().addLast(new WebSocketInitializer(handler, request.headers()));

        // 后续处理
        ctx.fireChannelRead(request.retain());
    }
}
