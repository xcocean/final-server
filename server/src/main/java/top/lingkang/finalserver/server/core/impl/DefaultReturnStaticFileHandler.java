package top.lingkang.finalserver.server.core.impl;

import cn.hutool.core.util.StrUtil;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultFileRegion;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.lingkang.finalserver.server.core.FinalServerConfiguration;
import top.lingkang.finalserver.server.core.FinalServerProperties;
import top.lingkang.finalserver.server.core.ReturnStaticFileHandler;
import top.lingkang.finalserver.server.web.http.FinalServerContext;
import top.lingkang.finalserver.server.web.http.HttpUtils;

import java.io.File;

/**
 * @author lingkang
 * created by 2023/3/31
 * @since 1.0.0
 * <p>
 * 默认返回静态文件处理，必要：断点续传
 */
public class DefaultReturnStaticFileHandler implements ReturnStaticFileHandler {
    private static final Logger log = LoggerFactory.getLogger(DefaultReturnStaticFileHandler.class);

    @Override
    public void returnStaticFile(FinalServerContext context, ChannelHandlerContext ctx) throws Exception {
        File file = context.getResponse().getResponseFile().getFile();
        if (!file.exists()) {
            log.warn("文件不存在：{}", file.getAbsolutePath());
            FinalServerConfiguration.webExceptionHandler.notHandler(ctx);
            return;
        }
        HttpHeaders headers = new DefaultHttpHeaders();
        headers.set(HttpHeaderNames.ACCEPT_RANGES, HttpHeaderValues.BYTES);
        headers.set(HttpHeaderNames.CONTENT_LENGTH, file.length());
        headers.set(HttpHeaderNames.LAST_MODIFIED, file.lastModified());
        headers.set(HttpHeaderNames.CACHE_CONTROL, FinalServerProperties.file_cache_control);
        // 设置文件请求头
        HttpUtils.setResponseHeadName(context.getResponse().getResponseFile(), headers);

        // 添加会话到cookie
        FinalServerConfiguration.httpSessionManage.bindCurrentSession(context);
        // 添加cookie
        HttpUtils.addHeaderCookie(context);
        // 设置用户设置的请求头，可以覆盖上面的设置
        headers.setAll(context.getResponse().getHeaders());

        // 304缓存  If-Modified-Since
        String timeMillis = context.getRequest().getHeaders().get(HttpHeaderNames.IF_MODIFIED_SINCE);
        if (timeMillis != null && timeMillis.equals(file.lastModified() + "")) {
            DefaultHttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_MODIFIED);
            response.headers().set(headers);
            ctx.writeAndFlush(response).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) {
                    future.channel().close();
                    if (context.getResponse().getResponseFile().isDelete())// 检查文件删除
                        file.delete();
                }
            });
            return;
        }


        long offset = 0L, length = file.length();
        HttpResponseStatus status = HttpResponseStatus.OK;

        /**
         * 需要断点续传协议: Content-Range: <unit> <range-start>-<range-end>/<size> 其中size是指整个文件的大小
         * Content-Range: https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Headers/Content-Range
         * Range: https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Headers/Range
         */
        String range = context.getRequest().getHeaders().get(HttpHeaderNames.RANGE);
        if (StrUtil.isNotBlank(range)) {// Range: bytes=1900544-  Range: bytes=1900544-6666666
            range = range.substring(6);
            String[] split = range.split("-");
            try {
                offset = Long.parseLong(split[0]);
                if (offset >= length) {
                    // 返回
                    HttpUtils.sendString(ctx, "服务器无法处理所请求的数据区间", 416);
                    return;
                }
                if (split.length == 2) {
                    long end = Long.parseLong(split[1]);
                    if (end < length) {
                        headers.set(HttpHeaderNames.CONTENT_RANGE, "bytes " + range + "/" + file.length());
                        length = end - offset + 1;
                    } else {
                        // 返回
                        HttpUtils.sendString(ctx, "服务器无法处理所请求的数据区间", 416);
                        return;
                    }
                } else {
                    headers.set(HttpHeaderNames.CONTENT_RANGE, "bytes " + offset + "-" + (length - 1) + "/" + file.length());
                    length = length - offset;
                }
                headers.set(HttpHeaderNames.CONTENT_LENGTH, length);// 重写响应长度
                status = HttpResponseStatus.PARTIAL_CONTENT; // 206
            } catch (Exception e) {
                log.warn("断点续传解析错误", e);
                // 返回 https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Status/416
                HttpUtils.sendString(ctx, "服务器无法处理所请求的数据区间", 416);
                return;
            }
        }

        DefaultHttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, status);
        response.headers().set(headers);

        ctx.write(response);
        // RandomAccessFile accessFile = new RandomAccessFile(file, "r");
        ctx.writeAndFlush(
                new DefaultFileRegion(file, offset, length), // 零拷贝，部分系统可能不支持，低版本jdk6以下存在bug
//                new ChunkedFile(
//                        accessFile, offset, length, 1024 * 64), // 正常发送文件块
                ctx.newProgressivePromise()
        ).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                future.channel().close();
                if (context.getResponse().getResponseFile().isDelete())// 检查文件删除
                    file.delete();
            }
        });
    }
}
