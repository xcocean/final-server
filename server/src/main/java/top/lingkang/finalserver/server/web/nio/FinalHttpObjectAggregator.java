package top.lingkang.finalserver.server.web.nio;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * @author lingkang
 * 2023/1/5
 * @since 1.0.0
 **/
public class FinalHttpObjectAggregator extends HttpObjectAggregator {
    private static final Logger logger = LoggerFactory.getLogger(FinalHttpObjectAggregator.class);
    private static final FullHttpResponse TOO_LARGE_CLOSE = new DefaultFullHttpResponse(
            HTTP_1_1, HttpResponseStatus.REQUEST_ENTITY_TOO_LARGE,
            Unpooled.wrappedBuffer("请求内容太大，它可能是文件".getBytes())
    );

    public FinalHttpObjectAggregator(int maxContentLength) {
        super(maxContentLength);
    }

    @Override
    protected void handleOversizedMessage(ChannelHandlerContext ctx, HttpMessage oversized) throws Exception {
        if (oversized instanceof HttpRequest) {
            // send back a 413 and close the connection

            // If the client started to send data already, close because it's impossible to recover.
            // If keep-alive is off and 'Expect: 100-continue' is missing, no need to leave the connection open.
            if (oversized instanceof FullHttpMessage ||
                    !HttpUtil.is100ContinueExpected(oversized) && !HttpUtil.isKeepAlive(oversized)) {
                ChannelFuture future = ctx.writeAndFlush(TOO_LARGE_CLOSE.retainedDuplicate());
                future.addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (!future.isSuccess()) {
                            logger.debug("Failed to send a 413 Request Entity Too Large.", future.cause());
                        }
                        ctx.close();
                    }
                });
            } else {
                HttpRequest request = (HttpRequest) oversized;
                logger.warn(
                        "此次异常的请求是 {} - {}\n请求内容太大，它可能是文件，请设置 server.maxContentLength 属性，例如：server.maxContentLength=0 为最大限制2GB，文件太大可考虑分段上传！",
                        request.method().name(),
                        request.uri()
                );
                logger.debug("详细信息：" + oversized);
                ctx.writeAndFlush(TOO_LARGE_CLOSE.retainedDuplicate()).addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) {
                        future.channel().close();
                        if (future.isSuccess()) {
                            ctx.close();
                        }
                    }
                });
            }
        } else if (oversized instanceof HttpResponse) {
            ctx.close();
            throw new TooLongHttpContentException("Response entity too large: " + oversized);
        } else {
            throw new IllegalStateException();
        }
    }
}
