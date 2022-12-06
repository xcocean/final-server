package top.lingkang.finalserver.server.web.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author lingkang
 * Created by 2022/12/6
 */
public class FirstHandler extends ByteToMessageDecoder {
    private ChannelPipeline pipeline;

    public FirstHandler(ChannelPipeline pipeline) {
        this.pipeline = pipeline;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        System.out.println(in.getClass());
        // ctx.fireChannelRead(in);
       // ctx.fireChannelRead(in);
        /*ByteBuf slice = in.slice(0, Math.min(in.readableBytes(), 512));
        byte[] bytes = byteBufToBytes(slice);
        System.out.println(new String(bytes));*/

    }

    public static byte[] byteBufToBytes(ByteBuf buf) {
        int length =buf.readableBytes();
        byte[] body =new byte[length];
        buf.readBytes(body);
        return body;
    }
}
