package top.lingkang.finalserver.server.web.ws;

/**
 * @author lingkang
 * Created by 2022/12/12
 * @since 1.0.0
 * 若关闭浏览器的tab页面后，仍未调用close？
 * 这是浏览器底层并未关闭websocket导致，例如360极速浏览器就有这个问题。可以改用google的Chrome、微软的Edge
 */
public interface WebSocketHandler {
    void onOpen(WebSocketSession session) throws Exception;

    void onMessage(WebSocketSession session,Message message) throws Exception;

    void onException(WebSocketSession session,Throwable cause)throws Exception;

    void onClose(WebSocketSession session)throws Exception;
}
