package top.lingkang.finalserver.server.web.nio;

import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.channel.socket.nio.NioSocketChannel;
import top.lingkang.finalserver.server.core.FinalServerConfiguration;

import java.nio.channels.SocketChannel;


/**
 * @author lingkang
 * Created by 2022/12/6
 * @since 1.0.0
 */
class FinalServerNioSocketChannel extends NioSocketChannel {
    public FinalServerNioSocketChannel(Channel parent, SocketChannel socket) {
        super(parent, socket);
    }

    private NioSocketChannel this_ = this;

    // 自定义ID
    @Override
    protected ChannelId newId() {
        return new ChannelId() {
            private String id = FinalServerConfiguration.idGenerateFactory.generateNettyId(this_);

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
