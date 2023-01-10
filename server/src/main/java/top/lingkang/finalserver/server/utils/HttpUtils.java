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
import top.lingkang.finalserver.server.web.http.FinalServerContext;
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
    private static void responseBeforeHandler(FullHttpResponse response) {
        FinalServerContext context = FinalServerContext.currentContext();
        if (context == null)// websocket 时，上下文为空
            return;

        // 添加会话到cookie
        FinalServerConfiguration.httpSessionManage.addSessionIdToCurrentHttp(context);
        // 添加cookie
        HttpUtils.addHeaderCookie(context);

        response.headers().setAll(context.getResponse().getHeaders());
    }

    public static void sendString(ChannelHandlerContext ctx, @NotNull String context, int status) {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, HttpResponseStatus.valueOf(status),
                Unpooled.copiedBuffer(context, CharsetUtil.UTF_8)
        );
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, context.getBytes().length);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
        responseBeforeHandler(response);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    public static void sendResponse(ChannelHandlerContext ctx, Response httpResponse, int status) {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, HttpResponseStatus.valueOf(status),
                Unpooled.copiedBuffer(httpResponse.getContent())
        );
        response.headers().set(httpResponse.getHeaders());
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, httpResponse.getContent().length);
        responseBeforeHandler(response);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    public static void sendResponse(ChannelHandlerContext ctx, String content, int status) {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, HttpResponseStatus.valueOf(status),
                Unpooled.copiedBuffer(content, CharsetUtil.UTF_8)
        );
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.length());
        responseBeforeHandler(response);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    public static void addHeaderCookie(FinalServerContext context) {
        if (!context.getResponse().getCookies().isEmpty()) {
            Set<Cookie> cookies = context.getResponse().getCookies();
            for (Cookie cookie : cookies) {
                context.getResponse().setHeader("Set-Cookie", ServerCookieEncoder.STRICT.encode(cookie));
            }
        }
    }

    public static String getRequestPathInfo(Request request) {
        return request.getHttpMethod().name() + "  path=" + request.getPath();
    }

    public static void closeHttpWebsocket(ChannelHandlerContext ctx, String msg) {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HTTP_1_1, HttpResponseStatus.BAD_REQUEST, Unpooled.wrappedBuffer(msg.getBytes()));
        response.headers().set(response.headers().set(FinalServerConfiguration.defaultResponseHeaders.get(false)));
        responseBeforeHandler(response);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
}
