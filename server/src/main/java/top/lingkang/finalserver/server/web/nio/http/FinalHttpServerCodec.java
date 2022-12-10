package top.lingkang.finalserver.server.web.nio.http;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.CombinedChannelDuplexHandler;
import io.netty.handler.codec.http.*;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

/**
 * @author lingkang
 * Created by 2022/12/10
 * 此类是对 的复制，{@link io.netty.handler.codec.http.HttpServerCodec}无任何修改
 * @since 1.0.0
 */
public class FinalHttpServerCodec extends CombinedChannelDuplexHandler<FinalHttpRequestDecoder, HttpResponseEncoder> implements HttpServerUpgradeHandler.SourceCodec {
    private final Queue<HttpMethod> queue;

    public FinalHttpServerCodec() {
        this(4096, 8192, 8192);
    }

    public FinalHttpServerCodec(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize) {
        this.queue = new ArrayDeque();
        this.init(new FinalHttpServerCodec.HttpServerRequestDecoder(maxInitialLineLength, maxHeaderSize, maxChunkSize), new FinalHttpServerCodec.HttpServerResponseEncoder());
    }

    public FinalHttpServerCodec(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean validateHeaders) {
        this.queue = new ArrayDeque();
        this.init(new FinalHttpServerCodec.HttpServerRequestDecoder(maxInitialLineLength, maxHeaderSize, maxChunkSize, validateHeaders), new FinalHttpServerCodec.HttpServerResponseEncoder());
    }

    public FinalHttpServerCodec(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean validateHeaders, int initialBufferSize) {
        this.queue = new ArrayDeque();
        this.init(new FinalHttpServerCodec.HttpServerRequestDecoder(maxInitialLineLength, maxHeaderSize, maxChunkSize, validateHeaders, initialBufferSize), new FinalHttpServerCodec.HttpServerResponseEncoder());
    }

    public FinalHttpServerCodec(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean validateHeaders, int initialBufferSize, boolean allowDuplicateContentLengths) {
        this.queue = new ArrayDeque();
        this.init(new FinalHttpServerCodec.HttpServerRequestDecoder(maxInitialLineLength, maxHeaderSize, maxChunkSize, validateHeaders, initialBufferSize, allowDuplicateContentLengths), new FinalHttpServerCodec.HttpServerResponseEncoder());
    }

    public FinalHttpServerCodec(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean validateHeaders, int initialBufferSize, boolean allowDuplicateContentLengths, boolean allowPartialChunks) {
        this.queue = new ArrayDeque();
        this.init(new FinalHttpServerCodec.HttpServerRequestDecoder(maxInitialLineLength, maxHeaderSize, maxChunkSize, validateHeaders, initialBufferSize, allowDuplicateContentLengths, allowPartialChunks), new FinalHttpServerCodec.HttpServerResponseEncoder());
    }

    public void upgradeFrom(ChannelHandlerContext ctx) {
        ctx.pipeline().remove(this);
    }

    private final class HttpServerResponseEncoder extends HttpResponseEncoder {
        private HttpMethod method;

        private HttpServerResponseEncoder() {
        }

        protected void sanitizeHeadersBeforeEncode(HttpResponse msg, boolean isAlwaysEmpty) {
            if (!isAlwaysEmpty && HttpMethod.CONNECT.equals(this.method) && msg.status().codeClass() == HttpStatusClass.SUCCESS) {
                msg.headers().remove(HttpHeaderNames.TRANSFER_ENCODING);
            } else {
                super.sanitizeHeadersBeforeEncode(msg, isAlwaysEmpty);
            }
        }

        protected boolean isContentAlwaysEmpty(HttpResponse msg) {
            this.method = (HttpMethod)FinalHttpServerCodec.this.queue.poll();
            return HttpMethod.HEAD.equals(this.method) || super.isContentAlwaysEmpty(msg);
        }
    }

    private final class HttpServerRequestDecoder extends FinalHttpRequestDecoder {
        HttpServerRequestDecoder(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize) {
            super(maxInitialLineLength, maxHeaderSize, maxChunkSize);
        }

        HttpServerRequestDecoder(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean validateHeaders) {
            super(maxInitialLineLength, maxHeaderSize, maxChunkSize, validateHeaders);
        }

        HttpServerRequestDecoder(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean validateHeaders, int initialBufferSize) {
            super(maxInitialLineLength, maxHeaderSize, maxChunkSize, validateHeaders, initialBufferSize);
        }

        HttpServerRequestDecoder(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean validateHeaders, int initialBufferSize, boolean allowDuplicateContentLengths) {
            super(maxInitialLineLength, maxHeaderSize, maxChunkSize, validateHeaders, initialBufferSize, allowDuplicateContentLengths);
        }

        HttpServerRequestDecoder(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean validateHeaders, int initialBufferSize, boolean allowDuplicateContentLengths, boolean allowPartialChunks) {
            super(maxInitialLineLength, maxHeaderSize, maxChunkSize, validateHeaders, initialBufferSize, allowDuplicateContentLengths, allowPartialChunks);
        }

        protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) throws Exception {
            int oldSize = out.size();
            super.decode(ctx, buffer, out);
            int size = out.size();

            for(int i = oldSize; i < size; ++i) {
                Object obj = out.get(i);
                if (obj instanceof HttpRequest) {
                    FinalHttpServerCodec.this.queue.add(((HttpRequest)obj).method());
                }
            }

        }
    }
}
