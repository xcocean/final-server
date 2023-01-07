package top.lingkang.finalserver.server.web.ws;

import io.netty.buffer.ByteBufUtil;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import top.lingkang.finalserver.server.constant.FinalServerConstants;

import java.nio.charset.Charset;

/**
 * @author lingkang
 * Created by 2022/12/12
 * @since 1.0.0
 */
public class Message {
    private WebSocketFrame webSocketFrame;

    public Message(WebSocketFrame webSocketFrame) {
        this.webSocketFrame = webSocketFrame;
    }

    public String getText() {
        return webSocketFrame.content().toString(Charset.forName(FinalServerConstants.encoding));
    }

    public byte[] getByte() {
        return ByteBufUtil.getBytes(webSocketFrame.content());
    }

    public WebSocketFrame getWebSocketFrame() {
        return webSocketFrame;
    }

    @Override
    public String toString() {
        return "Message{" +
                getText() +
                "}";
    }
}
