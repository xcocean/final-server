package top.lingkang.finalserver.server.web.http;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author lingkang
 * Created by 2022/12/6
 */
public class FinalServerContext {
    public FinalServerContext(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

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
}
