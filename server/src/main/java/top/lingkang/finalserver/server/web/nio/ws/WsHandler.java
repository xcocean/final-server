package top.lingkang.finalserver.server.web.nio.ws;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

/**
 * @author lingkang
 * Created by 2022/12/12
 */
public interface WsHandler {
    /**
     * 返回 true 时将不再处理下面的 WsHandler
     */
    boolean handler(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception;
}
