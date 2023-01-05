package top.lingkang.finalserver.server.web.http;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import top.lingkang.finalserver.server.core.FinalServerConfiguration;
import top.lingkang.finalserver.server.utils.HttpUtils;
import top.lingkang.finalserver.server.web.FinalServerInitializer;

import java.util.HashMap;


/**
 * @author lingkang
 * Created by 2022/12/6
 * @since 1.0.0
 */
class HandlerHttpRequest extends SimpleChannelInboundHandler<FinalServerContext> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FinalServerContext context) throws Exception {
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
                templateMap.put("session", FinalServerConfiguration.httpSessionManage.getSessionAttribute(context.getRequest()));
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
}
