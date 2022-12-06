package top.lingkang.finalserver.server.web.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.http.*;
import io.netty.handler.traffic.GlobalTrafficShapingHandler;
import top.lingkang.finalserver.server.utils.NetUtils;
import top.lingkang.finalserver.server.web.FinalServerWeb;

import java.util.List;

/**
 * @author lingkang
 * Created by 2022/12/6
 * 解码调度
 */
public class DecoderDispatcher extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if (byteBuf.readableBytes() < 5) {
            return;
        }

        ChannelPipeline pipeline = channelHandlerContext.pipeline();

        ByteBuf slice = byteBuf.slice(0, Math.min(byteBuf.readableBytes(), 256));
        byte[] header = ByteBufUtil.getBytes(slice);
        System.out.println(new String(header));
        if (NetUtils.isHttp(header[0], header[1])) {
            System.out.println(1);
        }
    }
}
