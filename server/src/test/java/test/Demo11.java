package test;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import top.lingkang.finalserver.server.core.FinalServerProperties;
import top.lingkang.finalserver.server.web.nio.FinalHttpObjectAggregator;

/**
 * @author lingkang
 * Created by 2023/1/14
 */
public class Demo11 {
    public static void main(String[] args) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup(1);
        try {
            ForwardRequestHandler forwardRequestHandler = new ForwardRequestHandler(null, null, null);
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new HttpClientCodec());
                            p.addLast(new FinalHttpObjectAggregator(FinalServerProperties.server_maxContentLength));
                            p.addLast(forwardRequestHandler);
                        }
                    });

            // Start the client.
            ChannelFuture f = b.connect("lingkang.top", 80).sync();

            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
            System.out.println(5);
            System.out.println(forwardRequestHandler.response != null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Shut down the event loop to terminate all threads.
            group.shutdownGracefully();
        }
    }

    static class ForwardRequestHandler extends SimpleChannelInboundHandler<FullHttpResponse> {
        private ChannelHandlerContext context;
        private FullHttpRequest request;
        private String forwardPath;
        public volatile FullHttpResponse response;


        public ForwardRequestHandler(ChannelHandlerContext context, FullHttpRequest request, String forwardPath) {
            this.context = context;
            this.request = request;
            this.forwardPath = forwardPath;
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            /*URI uri = new URI("/");
            FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_0, HttpMethod.GET, uri.toASCIIString());
            request.headers().add(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            request.headers().add(HttpHeaderNames.CONTENT_LENGTH, request.content().readableBytes());*/
            request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_0, HttpMethod.GET, "http://lingkang.top:80");
            request.setUri("/");
            ctx.writeAndFlush(request);
            System.out.println(1);
        }

        @Override
        public void channelRead0(ChannelHandlerContext ctx, FullHttpResponse response) throws Exception {
            this.response = response;
            System.out.println("response -> " + response);
            ByteBuf buf = response.content();
            String result = buf.toString(CharsetUtil.UTF_8);
            // System.out.println("response -> " + result);

            // context.writeAndFlush(FullHttpResponse.EMPTY_LAST_CONTENT);
            ctx.channel().writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) {
                    future.channel().close();
                }
            });
            ctx.fireChannelRead(response.retain());
            System.out.println(2);
        }
    }
}
