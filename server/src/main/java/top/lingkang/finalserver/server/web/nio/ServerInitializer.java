package top.lingkang.finalserver.server.web.nio;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import org.springframework.context.ApplicationContext;
import top.lingkang.finalserver.server.web.handler.ControllerHandler;
import top.lingkang.finalserver.server.web.http.FilterChain;
import top.lingkang.finalserver.server.web.http.HandlerHttpRequest;

/**
 * @author lingkang
 * Created by 2022/12/6
 */
public class ServerInitializer extends ChannelInitializer<Channel> {
    private ApplicationContext applicationContext;
    private FilterChain filterChain;

    public ServerInitializer(ApplicationContext applicationContext, FilterChain filterChain) {
        this.applicationContext = applicationContext;
        this.filterChain = filterChain;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(1024));
        pipeline.addLast(new HandlerHttpWrapper());
        pipeline.addLast(new HandlerHttpRequest(filterChain));
    }

}
