package top.lingkang.finalserver.server.web.nio.ws;

/**
 * @author lingkang
 * Created by 2022/12/12
 */
public interface WebSocketHandler {
    void onOpen(WebSocketSession session) throws Exception;

    void onMessage(WebSocketSession session,Message message) throws Exception;

    void onException(WebSocketSession session,Throwable cause)throws Exception;

    void onClose(WebSocketSession session)throws Exception;
}
