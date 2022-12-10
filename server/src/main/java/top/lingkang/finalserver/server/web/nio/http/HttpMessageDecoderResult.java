package top.lingkang.finalserver.server.web.nio.http;

import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpObjectDecoder;

/**
 * A {@link DecoderResult} for {@link HttpMessage}s as produced by an {@link HttpObjectDecoder}.
 * <p>
 * Please note that there is no guarantee that a {@link HttpObjectDecoder} will produce a {@link
 * io.netty.handler.codec.http.HttpMessageDecoderResult}. It may simply produce a regular {@link DecoderResult}. This result is intended for
 * successful {@link HttpMessage} decoder results.
 */
public final class HttpMessageDecoderResult extends DecoderResult {

    private final int initialLineLength;
    private final int headerSize;

    HttpMessageDecoderResult(int initialLineLength, int headerSize) {
        super(SIGNAL_SUCCESS);
        this.initialLineLength = initialLineLength;
        this.headerSize = headerSize;
    }

    /**
     * The decoded initial line length (in bytes), as controlled by {@code maxInitialLineLength}.
     */
    public int initialLineLength() {
        return initialLineLength;
    }

    /**
     * The decoded header size (in bytes), as controlled by {@code maxHeaderSize}.
     */
    public int headerSize() {
        return headerSize;
    }

    /**
     * The decoded initial line length plus the decoded header size (in bytes).
     */
    public int totalSize() {
        return initialLineLength + headerSize;
    }
}
