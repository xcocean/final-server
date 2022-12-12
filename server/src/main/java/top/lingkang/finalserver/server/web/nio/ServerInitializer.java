package top.lingkang.finalserver.server.web.nio;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.springframework.context.ApplicationContext;
import top.lingkang.finalserver.server.core.HttpParseTemplate;
import top.lingkang.finalserver.server.web.http.FilterChain;

/**
 * @author lingkang
 * Created by 2022/12/6
 * @since 1.0.0
 */
public class ServerInitializer extends ChannelInitializer<Channel> {
    private FilterChain filterChain;
    private HttpParseTemplate parseTemplate;

    public ServerInitializer(FilterChain filterChain, HttpParseTemplate parseTemplate) {
        this.filterChain = filterChain;
        this.parseTemplate = parseTemplate;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        //pipeline.addLast(new FinalHttpServerCodec());// 使用自定义http编解码
        pipeline.addLast(new HttpServerCodec());// http编解码
        // pipeline.addLast(new FinalHttpRequestDecoder());// http 解码
        // pipeline.addLast(new HttpRequestEncoder());// http 编码
        pipeline.addLast(new HttpObjectAggregator(1024));
        pipeline.addLast(new ChunkedWriteHandler());// 写内容
        pipeline.addLast(new HandlerHttpWrapper(filterChain, parseTemplate));// 进行一次包装
    }

}
