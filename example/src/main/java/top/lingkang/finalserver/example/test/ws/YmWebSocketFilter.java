package top.lingkang.finalserver.example.test.ws;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import org.springframework.stereotype.Component;
import top.lingkang.finalserver.server.web.nio.ws.FilterChain;
import top.lingkang.finalserver.server.web.nio.ws.WebSocketFilter;

/**
 * @author lingkang
 * Created by 2022/12/12
 */
@Component
public class YmWebSocketFilter implements WebSocketFilter {
    @Override
    public void doFilter(ChannelHandlerContext ctx, FullHttpRequest msg, FilterChain filterChain) throws Exception {
        System.out.println("ws之前");
        // filterChain.doFilter(ctx, msg);
        System.out.println("ws之后");
    }
}
