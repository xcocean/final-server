package top.lingkang.finalserver.server.web.http;


import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.stream.ChunkedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.lingkang.finalserver.server.core.FinalServerConfiguration;
import top.lingkang.finalserver.server.utils.CommonUtils;
import top.lingkang.finalserver.server.utils.HttpUtils;
import top.lingkang.finalserver.server.utils.NetUtils;

import java.io.RandomAccessFile;


/**
 * @author lingkang
 * Created by 2022/12/6
 */
public class HandlerHttpRequest extends SimpleChannelInboundHandler<FinalServerContext> {
    private static final Logger log = LoggerFactory.getLogger(HandlerHttpRequest.class);

    private FilterChain filterChain;

    public HandlerHttpRequest(FilterChain filterChain) {
        this.filterChain = filterChain;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FinalServerContext context) throws Exception {
        System.out.println(context.getRequest().getHttpMethod().name() + "  path=" + context.getRequest().getPath());

        filterChain.doFilter(context);
        if (context.getResponse().isReady()) {
            HttpResponse res = (HttpResponse) context.getResponse();
            if (res.isStatic()) {
                staticFile(res.getFilePath(), ctx);
                return;
            }
            HttpUtils.sendString(ctx, res, 200);
        } else {// 返回空值
            log.warn("此请求未做处理，将返回空值: " + NetUtils.getRequestPathInfo(context.getRequest()));
            HttpUtils.sendString(ctx, "", 200);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        FinalServerConfiguration.webExceptionHandler.exception(ctx, cause);
    }

    private void staticFile(String filePath, ChannelHandlerContext ctx) throws Exception {
        RandomAccessFile randomAccessFile = new RandomAccessFile(filePath, "r");
        DefaultHttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        response.headers().set(FinalServerConfiguration.defaultResponseHeaders);
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, randomAccessFile.length());
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, CommonUtils.getResponseHeadName(filePath));
        ctx.write(response);
        ctx.write(
                new ChunkedFile(randomAccessFile, 0, randomAccessFile.length(), 1024),
                ctx.newProgressivePromise());
        flushAndClose(ctx);
    }

    private void flushAndClose(ChannelHandlerContext ctx) {
        if (ctx.channel().isActive()) {
            ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }

    private void flushAndClose(Channel channel) {
        if (channel.isActive()) {
            channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }

}
