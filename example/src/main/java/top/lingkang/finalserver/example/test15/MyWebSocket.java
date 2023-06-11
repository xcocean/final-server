package top.lingkang.finalserver.example.test15;

import top.lingkang.finalserver.server.annotation.Websocket;
import top.lingkang.finalserver.server.web.ws.Message;
import top.lingkang.finalserver.server.web.ws.WebSocketHandler;
import top.lingkang.finalserver.server.web.ws.WebSocketSession;

/**
 * @author lingkang
 * Created by 2023/6/11
 */
@Websocket("/ws")
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
        session.write(message);

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
