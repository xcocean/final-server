package top.lingkang.finalserver.server.core.impl;

import cn.hutool.core.util.IdUtil;
import io.netty.channel.socket.nio.NioSocketChannel;
import top.lingkang.finalserver.server.core.IdGenerateFactory;
import top.lingkang.finalserver.server.web.http.Request;

/**
 * @author lingkang
 * Created by 2022/12/11
 * @since 1.0.0
 */
public class DefaultIdGenerateFactory implements IdGenerateFactory {
    @Override
    public String generateNettyId(NioSocketChannel nioSocketChannel) {
        return IdUtil.objectId();
    }

    @Override
    public String generateSessionId(Request request) {
        return IdUtil.fastUUID();
    }
}
