package top.lingkang.finalserver.server.web.nio.ws;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

/**
 * @author lingkang
 * Created by 2022/12/12
 */
public class FilterChain {
    private WebSocketFilter[] filters;
    private int length, current = 0;
    private WsHandler handler;

    public FilterChain(WebSocketFilter[] filters, WsHandler handler) {
        this.filters = filters;
        this.handler = handler;
        length = filters.length;
    }

    public boolean doFilter(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        if (current < length) {
            current++;// 自增
            try {
                filters[current - 1].doFilter(ctx, msg, this);
            } catch (Exception e) {
                throw e;
            }finally {
                current = 0;//reset
            }
        } else {
            current = 0;//reset
            handler.handler(ctx, msg);
            return true;
        }
        return false;
    }
}
