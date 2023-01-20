package top.lingkang.finalserver.server.web.http;

import cn.hutool.core.io.FileUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.lingkang.finalserver.server.core.FinalServerConfiguration;
import top.lingkang.finalserver.server.web.entity.ResponseFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
    private static final Logger log = LoggerFactory.getLogger(HttpUtils.class);

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
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=utf-8");
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
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=utf-8");
        responseBeforeHandler(response);
        context.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * 返回json字符串
     */
    public static void sendJSONString(ChannelHandlerContext context, String json, int statusCode) {
        sendJSONBytes(context, json == null ? null : json.getBytes(StandardCharsets.UTF_8), statusCode);
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
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json; charset=utf-8");
        context.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * 自定义返回httpResponse
     */
    public static void sendResponse(ChannelHandlerContext context, Response httpResponse, int statusCode) throws Exception {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, HttpResponseStatus.valueOf(statusCode),
                Unpooled.copiedBuffer(httpResponse.getContent())
        );
        response.headers().set(httpResponse.getHeaders());
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, httpResponse.getContent().length);
        responseBeforeHandler(response);
        context.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * 使用此方法时，应该手动设置响应头的内容长度：
     * response.headers().set(HttpHeaderNames.CONTENT_LENGTH, 1995);
     */
    public static void sendFullResponse(ChannelHandlerContext context, FullHttpResponse response, int statusCode) {
        if (response == null) {
            response = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1, HttpResponseStatus.valueOf(statusCode),
                    Unpooled.copiedBuffer(new byte[0])
            );
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, 0);
        }
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
                HttpUtils.getReturnFinalTemplateMap(FinalServerContext.currentContext()),
                FinalServerContext.currentContext()
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

    public static void setResponseHeadName(ResponseFile responseFile, HttpHeaders headers) throws Exception {
        String path = responseFile.getFile().getAbsolutePath();
        int index = path.lastIndexOf(".");
        if (index == -1) return;

        if (!headers.contains(HttpHeaderNames.CONTENT_TYPE)) {
            String type = StaticMimes.get(path.substring(index));
            headers.set(HttpHeaderNames.CONTENT_TYPE, type);

            // 是否为下载
            if (responseFile.isDownload() && !headers.contains(HttpHeaderNames.CONTENT_DISPOSITION)) {
                if (responseFile.getName() == null)// 设置名称
                    responseFile.setName(FileUtil.getName(path));
                headers.set(HttpHeaderNames.CONTENT_DISPOSITION, "attachment;filename=" + URLEncoder.encode(responseFile.getName(), "UTF-8"));
            }
        }
    }

    public static void closeHttpWebsocket(ChannelHandlerContext context, String msg) {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HTTP_1_1, HttpResponseStatus.BAD_REQUEST, Unpooled.wrappedBuffer(msg.getBytes()));
        response.headers().set(response.headers().set(FinalServerConfiguration.defaultResponseHeaders.get()));
        responseBeforeHandler(response);
        context.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * 返回模板的最终 map
     */
    public static Map<String, Object> getReturnFinalTemplateMap(FinalServerContext context) {
        // 模板全局map
        Map<String, Object> map = FinalServerContext.getTemplateGlobalMap();

        if (context.getResponse().getTemplateMap() != null)
            map.putAll(context.getResponse().getTemplateMap());

        // 添加固有参数
        map.put("request", context.getRequest());
        map.put("session", context.getRequest().getSession().getAttributeMap());
        return map;
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * 请求转发，使用http再次请求。存在局限性：
     * 例如不能转发文件下载、大数据内容（MB级别数据）
     */
    /*public static void httpForwardRequest(String forwardPath, Request request, ChannelHandlerContext ctx) throws Exception {
        try {
            HttpRequest httpRequest = HttpRequest.of("http://127.0.0.1:" + FinalServerProperties.server_port + forwardPath, StandardCharsets.UTF_8);
            for (String name : request.getHeaders().names()) {
                httpRequest.header(name, request.getHeader(name));
            }
            httpRequest.method(Method.valueOf(request.getHttpMethod().name()));
            Map<String, String> params = FinalServerContext.currentContext().getRequest().getParams();
            if (!params.isEmpty()) {
                String body = "";
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    body = "&" + entry.getKey() + "=" + entry.getValue();
                }
                httpRequest.body(body.substring(1));
            }

            HttpResponse execute = httpRequest.execute();
            List<String> list = execute.headers().get(null);
            int code = 200;
            if (list != null && !list.isEmpty()) {
                code = Integer.parseInt(list.get(0).split(" ")[1]);
            }

            FullHttpResponse response = new DefaultFullHttpResponse(
                    HTTP_1_1, HttpResponseStatus.valueOf(code), Unpooled.wrappedBuffer(execute.bodyBytes()));

            Map<String, List<String>> headers = execute.headers();
            for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
                if (entry.getKey() == null) {
                    continue;
                }
                response.headers().add(entry.getKey(), entry.getValue().get(0));
            }
            responseBeforeHandler(response);
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        } catch (Exception e) {
            log.error("请求转发失败，例如不能转发文件下载、大数据内容（MB级别数据）");
            throw e;
        }
    }*/
}
