package top.lingkang.finalserver.server.web.nio.ws;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

/**
 * @author lingkang
 * Created by 2022/12/12
 * @since 1.0.0
 */
public interface WsHandler {
    // 对 WebSocket 的处理
    void handler(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception;
}
