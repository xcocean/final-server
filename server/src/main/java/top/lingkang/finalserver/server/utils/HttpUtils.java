package top.lingkang.finalserver.server.utils;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import top.lingkang.finalserver.server.annotation.NotNull;
import top.lingkang.finalserver.server.web.http.HttpResponse;

/**
 * @author lingkang
 * Created by 2022/12/8
 */
public class HttpUtils {
    public static void sendString(ChannelHandlerContext ctx, @NotNull String context, int status) {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, HttpResponseStatus.valueOf(status),
                Unpooled.copiedBuffer(context, CharsetUtil.UTF_8)
        );
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, context.getBytes().length);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    public static void sendResponse(ChannelHandlerContext ctx, HttpResponse httpResponse, int status) {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, HttpResponseStatus.valueOf(status),
                Unpooled.copiedBuffer(httpResponse.getContent())
        );
        response.headers().set(httpResponse.getHeaders());
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, httpResponse.getContent().length);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }


}
