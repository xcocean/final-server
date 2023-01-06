package top.lingkang.finalserver.server.web.http;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import top.lingkang.finalserver.server.core.FinalServerConfiguration;
import top.lingkang.finalserver.server.utils.CommonUtils;
import top.lingkang.finalserver.server.utils.HttpUtils;
import top.lingkang.finalserver.server.web.FinalServerInitializer;

import java.util.HashMap;


/**
 * @author lingkang
 * Created by 2022/12/6
 * @since 1.0.0
 */
class HandlerHttpRequest extends SimpleChannelInboundHandler<FinalServerContext> {
    private FinalServerContext context;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FinalServerContext context) throws Exception {
        this.context = context;
        CommonUtils.pushWebListenerBefore(context);
        // 在此更新会话访问
        context.getRequest().getSession().updateLastAccessTime();
        // 过滤器
        new FilterChain(FinalServerInitializer.filters, FinalServerInitializer.requestHandlers).doFilter(context);

        if (context.getResponse().isReady()) {
            HttpResponse res = (HttpResponse) context.getResponse();
            if (res.isStatic()) {
                HttpHandler.returnStaticFile(res.getFilePath(), ctx, context);
                return;
            }

            if (res.isTemplate()) {
                // 将会话的值追加到目标渲染
                HashMap<String, Object> templateMap = res.getTemplateMap();
                if (templateMap == null)
                    templateMap = new HashMap<>();
                templateMap.put("request", context.getRequest());
                templateMap.put("session", context.getRequest().getSession().getAttributeMap());
                HttpUtils.sendResponse(
                        ctx,
                        FinalServerInitializer.httpParseTemplate.getTemplate(res.getTemplatePath(), templateMap),
                        res.getHeaders(),
                        200);
            } else
                HttpUtils.sendResponse(ctx, res, res.getCode());
        } else {// 返回 404
            FinalServerConfiguration.webExceptionHandler.notHandler(ctx);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        FinalServerConfiguration.webExceptionHandler.exception(ctx, cause);
        if (ctx.channel().isActive()) {// 未关闭时手动关闭
            HttpUtils.sendString(ctx, "", 500);
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);

        // 为了解决文件上传生成的临时文件导致内存溢出问题 https://github.com/netty/netty/issues/10351
        // 手动将文件释放
        context.getRequest().release();

        FinalServerContext.removeCurrentContext();

        // 监听：之后
        CommonUtils.pushWebListenerAfter();
    }
}
