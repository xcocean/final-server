package top.lingkang.finalserver.server.web.http;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import top.lingkang.finalserver.server.core.FinalServerConfiguration;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * @author lingkang
 * Created by 2022/12/8
 * @since 1.0.0
 * 统一处理http返回
 */
public final class HttpUtils {
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

    /**
     * 返回string字符串
     */
    public static void sendString(ChannelHandlerContext context, String content, int statusCode) {
        if (content == null)
            content = "";
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, HttpResponseStatus.valueOf(statusCode),
                Unpooled.copiedBuffer(bytes)
        );
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, bytes.length);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
        responseBeforeHandler(response);
        context.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * 返回空
     */
    public static void sendEmpty(ChannelHandlerContext context, int statusCode) {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, HttpResponseStatus.valueOf(statusCode),
                Unpooled.copiedBuffer(new byte[0])
        );
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, 0);
        // response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
        responseBeforeHandler(response);
        context.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * 返回json字符串
     */
    public static void sendJSON(ChannelHandlerContext context, String json, int statusCode) {
        if (json == null)
            sendJSONBytes(context, null, statusCode);
        else
            sendJSONBytes(context, json.getBytes(StandardCharsets.UTF_8), statusCode);
    }

    /**
     * 返回序列化的json对象
     */
    public static void sendJSONObject(ChannelHandlerContext context, Object jsonObject, int statusCode) {
        byte[] json = FinalServerConfiguration.serializable.jsonTo(jsonObject);
        sendJSONBytes(context, json, statusCode);
    }

    public static void sendJSONBytes(ChannelHandlerContext context, byte[] json, int statusCode) {
        if (json == null)
            json = new byte[0];
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, HttpResponseStatus.valueOf(statusCode),
                Unpooled.copiedBuffer(json)
        );
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, json.length);
        responseBeforeHandler(response);
        // 提升优先级
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json; charset=UTF-8");
        context.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * 自定义返回httpResponse
     */
    public static void sendResponse(ChannelHandlerContext context, Response httpResponse, int statusCode) {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, HttpResponseStatus.valueOf(statusCode),
                Unpooled.copiedBuffer(httpResponse.getContent())
        );
        response.headers().set(httpResponse.getHeaders());
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, httpResponse.getContent().length);
        responseBeforeHandler(response);
        context.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    public static void sendFullResponse(ChannelHandlerContext context, FullHttpResponse response, int statusCode) {
        if (response == null)
            response = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1, HttpResponseStatus.valueOf(statusCode),
                    Unpooled.copiedBuffer(new byte[0])
            );
        responseBeforeHandler(response);
        context.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * 返回html模板的内容
     */
    public static void sendTemplate(ChannelHandlerContext context, byte[] content, int statusCode) {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, HttpResponseStatus.valueOf(statusCode),
                Unpooled.copiedBuffer(content)
        );
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.length);
        responseBeforeHandler(response);
        context.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * 返回视图模板
     */
    public static void sendTemplate(ChannelHandlerContext context, String template, int statusCode) throws Exception {
        byte[] content = FinalServerConfiguration.httpParseTemplate.getTemplate(
                template,
                HttpUtils.getReturnFinalTemplateMap(FinalServerContext.currentContext())
        );

        sendTemplate(context, content, statusCode);
    }

    public static void addHeaderCookie(FinalServerContext context) {
        if (!context.getResponse().getCookies().isEmpty()) {
            Set<Cookie> cookies = context.getResponse().getCookies();
            for (Cookie cookie : cookies) {
                context.getResponse().setHeader("Set-Cookie", ServerCookieEncoder.STRICT.encode(cookie));
            }
        }
    }

    public static void closeHttpWebsocket(ChannelHandlerContext context, String msg) {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HTTP_1_1, HttpResponseStatus.BAD_REQUEST, Unpooled.wrappedBuffer(msg.getBytes()));
        response.headers().set(response.headers().set(FinalServerConfiguration.defaultResponseHeaders.get(false)));
        responseBeforeHandler(response);
        context.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * 返回模板的最终 map
     */
    public static Map<String, Object> getReturnFinalTemplateMap(FinalServerContext context) {
        // 将会话的值追加到目标渲染
        Map<String, Object> templateMap = context.getResponse().getTemplateMap();
        if (templateMap == null)
            templateMap = new HashMap<>();
        templateMap.put("request", context.getRequest());
        templateMap.put("session", context.getRequest().getSession().getAttributeMap());
        return templateMap;
    }
}
