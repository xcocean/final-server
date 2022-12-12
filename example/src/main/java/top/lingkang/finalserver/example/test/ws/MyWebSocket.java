package top.lingkang.finalserver.example.test.ws;

import top.lingkang.finalserver.server.annotation.Websocket;
import top.lingkang.finalserver.server.web.nio.ws.Message;
import top.lingkang.finalserver.server.web.nio.ws.WebSocketHandler;
import top.lingkang.finalserver.server.web.nio.ws.WebSocketSession;

/**
 * @author lingkang
 * Created by 2022/12/12
 */
@Websocket("/wss")
public class MyWebSocket implements WebSocketHandler {
    @Override
    public void onOpen(WebSocketSession session) throws Exception {
        // session.close();
    }

    @Override
    public void onMessage(WebSocketSession session, Message message) throws Exception {
        System.out.println(message);
        if ("1".equals(message.getText())) {
            session.close();
        }

    }

    @Override
    public void onException(WebSocketSession session, Throwable cause) throws Exception {
        cause.printStackTrace();
    }

    @Override
    public void onClose(WebSocketSession session) throws Exception {
        System.out.println("close: " + session.getId());
    }
}
