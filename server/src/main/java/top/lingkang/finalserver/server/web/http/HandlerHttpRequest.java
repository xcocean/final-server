package top.lingkang.finalserver.server.web.http;


import cn.hutool.core.util.StrUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.lingkang.finalserver.server.core.FinalServerConfiguration;
import top.lingkang.finalserver.server.core.FinalServerProperties;
import top.lingkang.finalserver.server.utils.CommonUtils;
import top.lingkang.finalserver.server.utils.HttpUtils;
import top.lingkang.finalserver.server.web.FinalServerInitializer;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;


/**
 * @author lingkang
 * Created by 2022/12/6
 * @since 1.0.0
 */
class HandlerHttpRequest extends SimpleChannelInboundHandler<FinalServerContext> {
    private static final Logger log = LoggerFactory.getLogger(HandlerHttpRequest.class);
    private FinalServerContext context;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FinalServerContext context) throws Exception {
        this.context = context;
        CommonUtils.pushWebListenerBefore(context);
        // 在此更新会话访问
        context.getRequest().getSession().updateLastAccessTime();
        // 过滤器
        new FilterChain(FinalServerInitializer.filters, FinalServerInitializer.requestHandlers).doFilter(context);

        if (context.getResponse().isReady()) {
            Response res = context.getResponse();
            if (res.getResponseFile() != null) {
                returnStaticFile(res.getResponseFile().getFilePath(), ctx);
                return;
            }

            if (res.isTemplate()) {
                // 将会话的值追加到目标渲染
                Map<String, Object> templateMap = res.getTemplateMap();
                if (templateMap == null)
                    templateMap = new HashMap<>();
                templateMap.put("request", context.getRequest());
                templateMap.put("session", context.getRequest().getSession().getAttributeMap());
                HttpUtils.sendTemplate(
                        ctx,
                        FinalServerConfiguration.httpParseTemplate.getTemplate(res.getTemplatePath(), templateMap),
                        200);
            } else // 其他内容
                HttpUtils.sendResponse(ctx, res, res.getStatusCode());
        } else {// 返回 404
            FinalServerConfiguration.webExceptionHandler.notHandler(ctx);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        FinalServerConfiguration.webExceptionHandler.exception(ctx, cause);
        if (ctx.channel().isActive()) {// 未关闭时手动关闭
            HttpUtils.sendString(ctx, "", 500);
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);

        // 为了解决文件上传生成的临时文件导致内存溢出问题 https://github.com/netty/netty/issues/10351
        // 手动将文件释放
        context.getRequest().release();

        FinalServerContext.removeCurrentContext();

        // 监听：之后
        CommonUtils.pushWebListenerAfter();
    }


    /**
     * 返回文件处理
     */
    private void returnStaticFile(String filePath, ChannelHandlerContext ctx) throws Exception {
        File file = new File(filePath);
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
        // 设置文件请求头
        CommonUtils.setResponseHeadName(context.getResponse().getResponseFile(), headers);

        // 添加会话到cookie
        FinalServerConfiguration.httpSessionManage.addSessionIdToCurrentHttp(context);
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

        // 检查文件删除
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                future.channel().close();
                if (context.getResponse().getResponseFile().isDelete())
                    file.delete();
            }
        });
    }
}
