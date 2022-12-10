package top.lingkang.finalserver.server.utils;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import top.lingkang.finalserver.server.web.http.Request;

/**
 * @author lingkang
 * Created by 2022/12/6
 * @since 1.0.0
 */
public class NetUtils {
    public static void send(ChannelHandlerContext ctx, String context, int status) {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, HttpResponseStatus.valueOf(status),
                Unpooled.copiedBuffer(context, CharsetUtil.UTF_8)
        );
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain;charset=UTF-8");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    public static String getRequestPathInfo(Request request) {
        return request.getHttpMethod().name() + "  path=" + request.getPath();
    }
}
