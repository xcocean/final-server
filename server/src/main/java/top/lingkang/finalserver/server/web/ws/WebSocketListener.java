package top.lingkang.finalserver.server.web.ws;

/**
 * @author lingkang
 * Created by 2023/1/7
 * @since 1.0.0
 * websocket 监听，优先级高于 websocket 的处理（重点），若提前在此处处理，可能会导致 websocket处理异常
 */
public interface WebSocketListener {
    /**
     * 有连接加入
     */
    void addConnect(WebSocketSession session);

    /**
     * 有连接发生异常
     */
    void exceptionConnect(WebSocketSession session, Throwable cause);

    /**
     * 有连接移除
     */
    void removeConnect(WebSocketSession session);
}
