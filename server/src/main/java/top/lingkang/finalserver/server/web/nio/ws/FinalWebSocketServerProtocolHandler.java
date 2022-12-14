package top.lingkang.finalserver.server.web.nio.ws;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.WebSocketDecoderConfig;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolConfig;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import top.lingkang.finalserver.server.core.FinalServerProperties;


/**
 * @author lingkang
 * Created by 2022/12/12
 */
public class FinalWebSocketServerProtocolHandler extends WebSocketServerProtocolHandler {

    public FinalWebSocketServerProtocolHandler(
            String websocketPath, String subprotocols,
            boolean allowExtensions, int maxFrameSize, boolean allowMaskMismatch, boolean checkStartsWith) {
//        super(websocketPath, subprotocols, allowExtensions, maxFrameSize, allowMaskMismatch, checkStartsWith,
//                FinalServerProperties.websocket_timeout);
        super(WebSocketServerProtocolConfig.newBuilder()
                .websocketPath(websocketPath)
                .subprotocols(subprotocols)
                .checkStartsWith(checkStartsWith)
                .handshakeTimeoutMillis(FinalServerProperties.websocket_timeout)
                .dropPongFrames(true)
                .decoderConfig(WebSocketDecoderConfig.newBuilder()
                        .maxFramePayloadLength(maxFrameSize)
                        .allowMaskMismatch(allowMaskMismatch)
                        .allowExtensions(allowExtensions)
                        .build())
                .build());
    }
}
