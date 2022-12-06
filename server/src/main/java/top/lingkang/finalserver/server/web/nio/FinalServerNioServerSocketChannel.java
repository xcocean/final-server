package top.lingkang.finalserver.server.web.nio;

import cn.hutool.core.util.IdUtil;
import io.netty.channel.ChannelId;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.internal.SocketUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.nio.channels.SocketChannel;
import java.util.List;

/**
 * @author lingkang
 * Created by 2022/12/6
 * 自定义id生成
 * MongoDB数据库的一种唯一ID生成策略，是UUID version1的变种
 */
public class FinalServerNioServerSocketChannel extends NioServerSocketChannel {
    private static final Logger logger=LoggerFactory.getLogger(FinalServerNioServerSocketChannel.class);

    @Override
    protected int doReadMessages(List<Object> buf) throws Exception {
        SocketChannel ch = SocketUtils.accept(javaChannel());

        try {
            if (ch != null) {
                NioSocketChannel nioSocketChannel = new FinalServerNioSocketChannel(this, ch);
                buf.add(nioSocketChannel);
                return 1;
            }
        } catch (Throwable t) {
            logger.warn("Failed to create a new channel from an accepted socket.", t);

            try {
                ch.close();
            } catch (Throwable t2) {
                logger.warn("Failed to close a socket.", t2);
            }
        }

        return 0;
    }
}
