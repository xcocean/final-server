package top.lingkang.finalserver.example.test.config;

import org.springframework.stereotype.Component;
import top.lingkang.finalserver.server.web.ws.WebSocketListener;
import top.lingkang.finalserver.server.web.ws.WebSocketSession;

/**
 * @author lingkang
 * Created by 2023/1/7
 */
@Component
public class MyWebSocketListener implements WebSocketListener {
    @Override
    public void addConnect(WebSocketSession session) {
        System.out.println("有连接加入: " + session.getId());
    }

    @Override
    public void exceptionConnect(WebSocketSession session, Throwable cause) {
        System.out.println("有连接异常: " + session.getId());
    }

    @Override
    public void removeConnect(WebSocketSession session) {
        System.out.println("有连接断开: " + session.getId());
    }
}
