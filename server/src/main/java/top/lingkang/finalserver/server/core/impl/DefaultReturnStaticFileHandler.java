package top.lingkang.finalserver.server.core.impl;

import cn.hutool.core.util.StrUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.lingkang.finalserver.server.core.FinalServerConfiguration;
import top.lingkang.finalserver.server.core.FinalServerProperties;
import top.lingkang.finalserver.server.core.ReturnStaticFileHandler;
import top.lingkang.finalserver.server.web.http.FinalServerContext;
import top.lingkang.finalserver.server.web.http.HttpUtils;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Date;

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
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
        HttpResponseStatus status = HttpResponseStatus.OK;
        HttpHeaders headers = new DefaultHttpHeaders();
        headers.set(HttpHeaderNames.ACCEPT_RANGES, HttpHeaderValues.BYTES);
        headers.set(HttpHeaderNames.CONTENT_LENGTH, randomAccessFile.length());
        headers.set(HttpHeaderNames.LAST_MODIFIED, new Date(file.lastModified()));
        headers.set(HttpHeaderNames.CACHE_CONTROL, HttpHeaderValues.NO_CACHE);
        // 设置文件请求头
        HttpUtils.setResponseHeadName(context.getResponse().getResponseFile(), headers);

        // 添加会话到cookie
        FinalServerConfiguration.httpSessionManage.bindCurrentSession(context);
        // 添加cookie
        HttpUtils.addHeaderCookie(context);
        // 设置用户设置的请求头，可以覆盖上面的设置
        headers.setAll(context.getResponse().getHeaders());

        // 静态文件需要做到断点续传
        String range = context.getRequest().getHeaders().get(HttpHeaderNames.RANGE);
        long offset = 0L, length = randomAccessFile.length();
        if (StrUtil.isNotBlank(range) && length > FinalServerProperties.server_fileFtpSize) {// Range: bytes=1900544-  Range: bytes=1900544-6666666
            range = range.substring(6);
            String[] split = range.split("-");
            try {
                offset = Long.parseLong(split[0]);
                if (split.length > 1 && StrUtil.isNotEmpty(split[1])) {
                    long end = Long.parseLong(split[1]);
                    if (end <= length && offset >= end) {
                        long endIndex = end - offset;
                        headers.set(HttpHeaderNames.CONTENT_RANGE, "bytes " + offset + "-" + endIndex + "/" + length);
                        length = endIndex - offset;
                    }
                } else {
                    headers.set(HttpHeaderNames.CONTENT_RANGE, "bytes " + offset + "-" + (length + offset - 1) + "/" + (offset + length));
                    length = length - offset;
                }
                headers.set(HttpHeaderNames.CONTENT_LENGTH, length);// 重写响应长度
                status = HttpResponseStatus.PARTIAL_CONTENT; // 206
            } catch (Exception e) {
                log.warn("断点续传解析错误", e);
            }
        }

        DefaultHttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, status);
        response.headers().set(headers);

        ctx.write(response);
        ctx.writeAndFlush(
                new ChunkedFile(randomAccessFile, offset, length, 1024),
                ctx.newProgressivePromise()
        );
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                future.channel().close();
                if (context.getResponse().getResponseFile().isDelete())// 检查文件删除
                    file.delete();
            }
        });
    }
}
