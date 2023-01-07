package top.lingkang.finalserver.server.web.ws;

import java.util.HashMap;

/**
 * @author lingkang
 * Created by 2023/1/7
 */
public class WebSocketManage {
    /**
     * 获取所有 WebSocketSession
     */
    public static HashMap<String, WebSocketSession> getAllWebSocketSession() {
        return WebSocketDispatchManage.sessionMap;
    }

    /**
     * 获取在线的 websocket 数量
     */
    public static int getOnlineNumber() {
        return WebSocketDispatchManage.sessionMap.size();
    }
}
