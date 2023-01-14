package top.lingkang.finalserver.server.web.http;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author lingkang
 * Created by 2022/12/6
 * @since 1.0.0
 * 上下文会再连接断开时移除
 */
public class FinalServerContext {
    public FinalServerContext(ChannelHandlerContext ctx) {
        this.ctx = ctx;
        finalServerContext.set(this);
    }

    public static final ThreadLocal<FinalServerContext> finalServerContext = new ThreadLocal<>();
    private Request request;
    private Response response;
    private ChannelHandlerContext ctx;

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    // 获取当前的上下文
    public static FinalServerContext currentContext() {
        return finalServerContext.get();
    }

    // 移除当前的上下文，框架会自动处理
    public static void removeCurrentContext() {
        finalServerContext.remove();
    }
}
