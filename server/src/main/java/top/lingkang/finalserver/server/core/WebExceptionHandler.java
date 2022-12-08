package top.lingkang.finalserver.server.core;

import io.netty.channel.ChannelHandlerContext;
import top.lingkang.finalserver.server.web.http.FinalServerContext;

/**
 * @author lingkang
 * Created by 2022/12/8
 */
public interface WebExceptionHandler {

    void exception(ChannelHandlerContext context, Throwable e)throws Exception;
}
