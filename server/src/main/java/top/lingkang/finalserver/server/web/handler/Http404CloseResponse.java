package top.lingkang.finalserver.server.web.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import top.lingkang.finalserver.server.utils.NetUtils;

/**
 * @author lingkang
 * Created by 2022/12/6
 */
public class Http404CloseResponse extends SimpleChannelInboundHandler<FullHttpRequest> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        NetUtils.send(ctx, "页面不存在或者无效的请求", HttpResponseStatus.NOT_FOUND);
    }

}
