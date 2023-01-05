package top.lingkang.finalserver.server.web.nio;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.lingkang.finalserver.server.core.FinalServerConfiguration;
import top.lingkang.finalserver.server.error.ContentTooLargeException;

/**
 * @author lingkang
 * 2023/1/5
 * @since 1.0.0
 **/
public class FinalHttpObjectAggregator extends HttpObjectAggregator {
    private static final Logger logger= LoggerFactory.getLogger(FinalHttpObjectAggregator.class);
    private static final FullHttpResponse TOO_LARGE_CLOSE = new DefaultFullHttpResponse(
            HttpVersion.HTTP_1_1, HttpResponseStatus.REQUEST_ENTITY_TOO_LARGE, Unpooled.EMPTY_BUFFER);

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
                FinalServerConfiguration.webExceptionHandler.exception(ctx,new ContentTooLargeException("请求内容太大，拒绝处理。The request content is too large, refused to process"));
                /*ctx.writeAndFlush(TOO_LARGE.retainedDuplicate()).addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (!future.isSuccess()) {
                            logger.debug("Failed to send a 413 Request Entity Too Large.", future.cause());
                            ctx.close();
                        }
                    }
                });*/
            }
        } else if (oversized instanceof HttpResponse) {
            ctx.close();
            throw new TooLongHttpContentException("Response entity too large: " + oversized);
        } else {
            throw new IllegalStateException();
        }
    }
}
