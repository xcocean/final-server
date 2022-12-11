package top.lingkang.finalserver.server.web.nio.ws;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

/**
 * @author lingkang
 * Created by 2022/12/12
 */
public interface WebsocketFilter {

    void doFilter(ChannelHandlerContext ctx, FullHttpRequest msg, FilterChain filterChain)throws Exception;
}
