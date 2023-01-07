package top.lingkang.finalserver.example.test.ws;

import top.lingkang.finalserver.server.annotation.Websocket;
import top.lingkang.finalserver.server.web.ws.Message;
import top.lingkang.finalserver.server.web.ws.WebSocketHandler;
import top.lingkang.finalserver.server.web.ws.WebSocketManage;
import top.lingkang.finalserver.server.web.ws.WebSocketSession;

import java.util.HashMap;

/**
 * @author lingkang
 * Created by 2022/12/12
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
        System.out.println("当前在线人数："+WebSocketManage.getOnlineNumber());

        // 获取所有连接的 WebSocketSession
        HashMap<String, WebSocketSession> map = WebSocketManage.getAllWebSocketSession();
        System.out.println(map);

        // 会话设置属性和获取属性
        session.setAttribute("username","lk");
        System.out.println(session.getAttribute("username"));
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
