package top.lingkang.finalserver.server.web.nio;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;
import top.lingkang.finalserver.server.core.FinalServerProperties;
import top.lingkang.finalserver.server.web.http.HandlerHttpWrapper;

/**
 * @author lingkang
 * Created by 2022/12/6
 * @since 1.0.0
 */
public class HandlerNioInitializer extends ChannelInitializer<Channel> {

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new HttpServerCodec());// http编解码
        // pipeline.addLast(new FinalHttpRequestDecoder());// http 解码
        // pipeline.addLast(new HttpRequestEncoder());// http 编码
        pipeline.addLast(new FinalHttpObjectAggregator(FinalServerProperties.server_maxContentLength));
        pipeline.addLast(new HandlerHttpWrapper());// 进行一次包装
    }

}
