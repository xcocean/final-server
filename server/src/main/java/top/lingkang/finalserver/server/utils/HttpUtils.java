package top.lingkang.finalserver.server.utils;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import io.netty.util.CharsetUtil;
import top.lingkang.finalserver.server.annotation.NotNull;
import top.lingkang.finalserver.server.core.FinalServerConfiguration;
import top.lingkang.finalserver.server.web.http.HttpResponse;
import top.lingkang.finalserver.server.web.http.Request;
import top.lingkang.finalserver.server.web.http.Response;

import java.util.Set;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * @author lingkang
 * Created by 2022/12/8
 * @since 1.0.0
 */
public class HttpUtils {
    public static void sendString(ChannelHandlerContext ctx, @NotNull String context, int status) {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, HttpResponseStatus.valueOf(status),
                Unpooled.copiedBuffer(context, CharsetUtil.UTF_8)
        );
        response.headers().set(FinalServerConfiguration.defaultResponseHeaders.get());
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, context.getBytes().length);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
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

    public static void sendResponse(ChannelHandlerContext ctx, byte[] content, HttpHeaders headers, int status) {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, HttpResponseStatus.valueOf(status),
                Unpooled.copiedBuffer(content)
        );
        response.headers().set(headers);
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.length);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    public static void addHeaderCookie(Response response) {
        if (!response.getCookies().isEmpty()) {
            Set<Cookie> cookies = response.getCookies();
            for (Cookie cookie : cookies) {
                response.setHeader("Set-Cookie", ServerCookieEncoder.STRICT.encode(cookie));
            }
        }
    }

    public static String getRequestPathInfo(Request request) {
        return request.getHttpMethod().name() + "  path=" + request.getPath();
    }

    public static void closeHttpWebsocket(ChannelHandlerContext ctx, String msg) {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HTTP_1_1, HttpResponseStatus.BAD_REQUEST, Unpooled.wrappedBuffer(msg.getBytes()));
        response.headers().set(response.headers().set(FinalServerConfiguration.defaultResponseHeaders.get()));
        ctx.channel().writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
}
