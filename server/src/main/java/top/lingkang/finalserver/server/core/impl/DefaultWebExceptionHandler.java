package top.lingkang.finalserver.server.core.impl;

import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.lingkang.finalserver.server.core.WebExceptionHandler;
import top.lingkang.finalserver.server.utils.HttpUtils;

/**
 * @author lingkang
 * Created by 2022/12/8
 * @since 1.0.0
 */
public class DefaultWebExceptionHandler implements WebExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(DefaultWebExceptionHandler.class);

    @Override
    public void exception(ChannelHandlerContext context, Throwable cause) throws Exception {
        log.error("web处理异常", cause);
        HttpUtils.sendString(context, "服务错误", 500);
    }
}
