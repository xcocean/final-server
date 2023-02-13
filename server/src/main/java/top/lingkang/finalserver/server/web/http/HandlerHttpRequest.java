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
import top.lingkang.finalserver.server.error.FinalServerException;
import top.lingkang.finalserver.server.utils.BeanUtils;
import top.lingkang.finalserver.server.utils.CommonUtils;
import top.lingkang.finalserver.server.web.FinalServerInitializer;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Date;


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
        try {
            CommonUtils.pushWebListenerBefore(context);
            Request request = context.getRequest();
            // 在此更新会话访问
            request.getSession().updateLastAccessTime();
            // 过滤器、处理链
            new FilterChain(FinalServerInitializer.filters, FinalServerInitializer.requestHandlers).doFilter(context);
            Response response = context.getResponse();
            if (response.isReady()) {
                if (response.getResponseFile() != null) {
                    returnStaticFile(response.getResponseFile().getFile(), ctx);
                    return;
                }

                if (response.isTemplate()) {
                    HttpUtils.sendTemplate(
                            ctx,
                            FinalServerConfiguration.httpParseTemplate.getTemplate(
                                    response.getTemplatePath(),
                                    HttpUtils.getReturnFinalTemplateMap(context), FinalServerContext.templateGlobalMap,context),
                            200);
                } else { // 其他内容
                    if (response.getForwardPath() != null) {// 是否使用了转发
                        if (request.getPath().equals(response.getForwardPath())) {
                            throw new FinalServerException("006请求路径不能与转发路径相同！请求路径：" + request.getPath() + "  转发路径：" + request.getPath());
                        }
                        request.getFullHttpRequest().setUri(response.getForwardPath());
                        // 修改转发参数初始化 // 追加转发参数
                        QueryStringDecoder queryUri = (QueryStringDecoder) BeanUtils.getAttributeValue(request, "queryUri");
                        BeanUtils.updateAttributeValue(queryUri, "path", response.getForwardPath());
                        BeanUtils.updateAttributeValue(request, "queryUri", queryUri);
                        BeanUtils.updateAttributeValue(response, "isReady", false);
                        // 过滤器、处理链
                        new FilterChain(FinalServerInitializer.filters, FinalServerInitializer.requestHandlers).doFilter(context);
                    }
                    HttpUtils.sendResponse(ctx, response, response.getStatusCode());
                }
            } else {// 返回 404
                FinalServerConfiguration.webExceptionHandler.notHandler(ctx);
            }
        } catch (Exception e) {
            FinalServerConfiguration.webExceptionHandler.exception(ctx, e);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // http处理后仍出现异常
        log.error("http异常处理后仍出现异常，请检查异常处理是否正确：", cause);
        if (ctx.channel().isActive()) {// 未关闭时手动关闭
            HttpUtils.sendString(ctx, "服务错误", 500);
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
    private void returnStaticFile(File file, ChannelHandlerContext ctx) throws Exception {
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
