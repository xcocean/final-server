package top.lingkang.finalserver.server.web.nio.ws;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

/**
 * @author lingkang
 * Created by 2022/12/12
 */
public interface WebSocketFilter {

    /**
     * 必须调用 filterChain.doFilter(ctx, msg); 之后才能继续握手连接，否则连接将关闭。
     * websocket连接时，不直接返回响应内容
     */
    void doFilter(ChannelHandlerContext ctx, FullHttpRequest msg, FilterChain filterChain)throws Exception;
}
