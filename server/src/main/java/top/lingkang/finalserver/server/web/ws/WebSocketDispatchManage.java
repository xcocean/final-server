package top.lingkang.finalserver.server.web.ws;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author lingkang
 * Created by 2023/1/7
 * @since 1.0.0
 */
final class WebSocketDispatchManage {
    public static final HashMap<String, WebSocketSession> sessionMap = new HashMap<>();
    public static final List<WebSocketListener> listener = new ArrayList<>();

    public static void addConnect(WebSocketSession session) {
        for (WebSocketListener webSocketListener : listener) {
            webSocketListener.addConnect(session);
        }
    }

    public static void exceptionConnect(WebSocketSession session, Throwable cause) {
        for (WebSocketListener webSocketListener : listener) {
            webSocketListener.exceptionConnect(session, cause);
        }
    }

    public static void removeConnect(WebSocketSession session) {
        for (WebSocketListener webSocketListener : listener) {
            webSocketListener.removeConnect(session);
        }
    }
}
