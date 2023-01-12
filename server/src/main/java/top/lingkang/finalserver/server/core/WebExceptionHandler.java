package top.lingkang.finalserver.server.core;

import io.netty.channel.ChannelHandlerContext;
import top.lingkang.finalserver.server.web.http.FinalServerContext;

/**
 * @author lingkang
 * Created by 2022/12/8
 * @since 1.0.0
 * 异常处理，应该使用 HttpUtils.send* 来返回异常处理后的结果
 */
public interface WebExceptionHandler {
    void exception(ChannelHandlerContext context, Throwable e)throws Exception;

    // 请求未找到handler时，即为 404 请求
    void notHandler(ChannelHandlerContext context)throws Exception;
}
