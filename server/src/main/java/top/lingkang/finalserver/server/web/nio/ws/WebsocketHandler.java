package top.lingkang.finalserver.server.web.nio.ws;

/**
 * @author lingkang
 * Created by 2022/12/12
 */
public interface WebsocketHandler {
    void onOpen() throws Exception;

    void onMessage() throws Exception;

    void onException(Throwable cause)throws Exception;

    void onClose()throws Exception;
}
