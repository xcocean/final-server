package top.lingkang.finalserver.server.web.nio.ws;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

/**
 * @author lingkang
 * Created by 2022/12/12
 */
public class FilterChain {
    private WebsocketFilter[] filters;
    private int length = 0, current = 0;
    private WsHandler[] handlers;

    public FilterChain(WebsocketFilter[] filters, WsHandler[] handlers) {
        this.filters = filters;
        this.handlers = handlers;
        length = filters.length;
    }

    public void doFilter(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        if (current < length) {
            current++;// 自增
            try {
                filters[current - 1].doFilter(ctx, msg, this);
            } catch (Exception e) {
                current = 0;//reset
                throw e;
            }
        } else {
            current = 0;//reset
            // 在此处调用处理逻辑方法
            for (WsHandler handler : handlers) {
                if (handler.handler(ctx, msg)) break;
            }
        }
    }

    public WsHandler[] getHandlers() {
        return handlers;
    }
}
