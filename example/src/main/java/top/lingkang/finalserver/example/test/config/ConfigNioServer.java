package top.lingkang.finalserver.example.test.config;

import io.netty.channel.ChannelId;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.context.annotation.Configuration;
import top.lingkang.finalserver.server.web.nio.FinalServerNioServerSocketChannel;

/**
 * @author lingkang
 * Created by 2022/12/6
 */
// @Configuration
public class ConfigNioServer extends FinalServerNioServerSocketChannel {
    @Override
    public ChannelId newId() {
        return super.newId();
    }
}
