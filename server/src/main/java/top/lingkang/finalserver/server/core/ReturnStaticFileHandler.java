package top.lingkang.finalserver.server.core;

import io.netty.channel.ChannelHandlerContext;
import top.lingkang.finalserver.server.web.http.FinalServerContext;

public interface ReturnStaticFileHandler {
    void returnStaticFile(FinalServerContext context, ChannelHandlerContext ctx) throws Exception;
}
