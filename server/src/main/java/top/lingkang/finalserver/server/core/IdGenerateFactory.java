package top.lingkang.finalserver.server.core;

import io.netty.channel.socket.nio.NioSocketChannel;
import top.lingkang.finalserver.server.web.http.Request;

/**
 * @author lingkang
 * Created by 2022/12/11
 * @since 1.0.0
 * 默认的id生成器
 */
public interface IdGenerateFactory {
    /**
     * 生成netty的ID
     */
    String generateNettyId(NioSocketChannel nioSocketChannel);

    /**
     * 生成 session ID
     */
    String generateSessionId(Request request);
}
