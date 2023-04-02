package top.lingkang.finalserver.server.core.impl;

import com.alibaba.fastjson2.JSONObject;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpHeaderNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.lingkang.finalserver.server.core.WebExceptionHandler;
import top.lingkang.finalserver.server.web.http.FinalServerContext;
import top.lingkang.finalserver.server.web.http.HttpUtils;
import top.lingkang.finalserver.server.web.http.Request;

/**
 * @author lingkang
 * Created by 2022/12/8
 * @since 1.0.0
 */
public class DefaultWebExceptionHandler implements WebExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(DefaultWebExceptionHandler.class);

    @Override
    public void exception(ChannelHandlerContext context, Throwable cause) throws Exception {
        log.error("默认web异常输出：", cause);
        String msg = getErrorMsg(cause);
        String type = FinalServerContext.currentContext().getRequest().getHeaders().get(HttpHeaderNames.CONTENT_TYPE);
        if (type != null && type.toLowerCase().contains("application")) {
            JSONObject json = new JSONObject();
            json.put("code", 500);
            json.put("msg", msg);
            json.put("timestamp", System.currentTimeMillis());
            HttpUtils.sendJSONObject(context, json, 500);
        } else {
            HttpUtils.sendString(context, msg, 500);
        }
    }

    @Override
    public void notHandler(ChannelHandlerContext context) throws Exception {
        Request request = FinalServerContext.currentContext().getRequest();
        log.warn("此请求未找到处理，将返回404: " + request.getHttpMethod().name() + "  path=" + request.getPath());
        HttpUtils.sendString(context, "404", 404);
    }

    public String getErrorMsg(Throwable cause) {
        if (cause instanceof NullPointerException)
            return "空指针异常";
        return cause.getMessage() != null ? cause.getMessage() : cause.getCause().getMessage();
    }
}
