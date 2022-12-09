package top.lingkang.finalserver.server.web.http;

import cn.hutool.core.util.StrUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.lingkang.finalserver.server.core.FinalServerConfiguration;
import top.lingkang.finalserver.server.utils.CommonUtils;

import java.io.RandomAccessFile;

/**
 * @author lingkang
 * Created by 2022/12/9
 * @since 1.0.0
 */
class HttpHandler {
    private static final Logger log= LoggerFactory.getLogger(HttpHandler.class);

    /**
     * 处理静态文件返回
     */
    public static void returnStaticFile(String filePath, ChannelHandlerContext ctx, Request request) throws Exception {
        RandomAccessFile randomAccessFile = new RandomAccessFile(filePath, "r");
        HttpResponseStatus status = HttpResponseStatus.OK;
        HttpHeaders headers = new DefaultHttpHeaders();
        headers.set(FinalServerConfiguration.defaultResponseHeaders);
        headers.set(HttpHeaderNames.ACCEPT_RANGES, HttpHeaderValues.BYTES);
        headers.set(HttpHeaderNames.CONTENT_LENGTH, randomAccessFile.length());
        CommonUtils.setResponseHeadName(filePath, headers);

        // 静态文件需要做到断点续传
        String range = request.getHeaders().get(HttpHeaderNames.RANGE);
        Long offset = 0L, length = randomAccessFile.length();
        if (StrUtil.isNotBlank(range)) {// Range: bytes=1900544-  Range: bytes=1900544-6666666
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
                status = HttpResponseStatus.PARTIAL_CONTENT;
            } catch (Exception e) {
                log.warn("断点续传解析错误", e);
            }
        }
        DefaultHttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, status);
        response.headers().set(headers);
        ctx.write(response);
        ctx.write(
                new ChunkedFile(randomAccessFile, offset, length, 1024),
                ctx.newProgressivePromise());
        flushAndClose(ctx);
    }

    private static void flushAndClose(ChannelHandlerContext ctx) {
        if (ctx.channel().isActive()) {
            ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }
}
