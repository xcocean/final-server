package top.lingkang.finalserver.server.web;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import org.springframework.context.ApplicationContext;
import top.lingkang.finalserver.server.web.handler.FirstHandler;

import java.util.Arrays;

/**
 * @author lingkang
 * Created by 2022/12/6
 */
public class ServerInitializer extends ChannelInitializer<Channel> {
    private ApplicationContext applicationContext;

    public ServerInitializer(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        String[] filters = applicationContext.getBeanNamesForType(Filter.class);
        System.out.println(Arrays.toString(filters));
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(1024));
        pipeline.addLast(new HandlerHttpRequest());
    }

}
