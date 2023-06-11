package top.lingkang.finalserver.server.web.nio;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpServerCodec;
import top.lingkang.finalserver.server.core.FinalServerConfiguration;
import top.lingkang.finalserver.server.web.http.DispatcherHandler;

/**
 * @author lingkang
 * Created by 2022/12/6
 * @since 1.0.0
 */
public class WsHandlerNioInitializer extends ChannelInitializer<Channel> {

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new HttpServerCodec());// http编解码
        pipeline.addLast(new FinalHttpObjectAggregator(FinalServerConfiguration.maxContentLength));
        pipeline.addLast(new DispatcherHandler());
    }

}
