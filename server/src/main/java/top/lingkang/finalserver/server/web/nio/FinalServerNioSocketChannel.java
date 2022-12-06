package top.lingkang.finalserver.server.web.nio;

import cn.hutool.core.util.IdUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.nio.channels.SocketChannel;


/**
 * @author lingkang
 * Created by 2022/12/6
 */
public class FinalServerNioSocketChannel extends NioSocketChannel {
    public FinalServerNioSocketChannel(Channel parent, SocketChannel socket) {
        super(parent, socket);
    }

    // 自定义ID
    @Override
    protected ChannelId newId() {
        return new ChannelId() {
            private String id = IdUtil.objectId();

            @Override
            public String asShortText() {
                return id;
            }

            @Override
            public String asLongText() {
                return id;
            }

            @Override
            public int compareTo(ChannelId o) {
                return id.equals(o.asLongText()) ? 1 : 0;
            }
        };
    }
}
