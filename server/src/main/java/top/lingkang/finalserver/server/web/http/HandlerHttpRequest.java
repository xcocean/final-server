package top.lingkang.finalserver.server.web.http;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.lingkang.finalserver.server.core.FinalServerConfiguration;
import top.lingkang.finalserver.server.utils.HttpUtils;
import top.lingkang.finalserver.server.web.FinalServerHttpContext;
import top.lingkang.finalserver.server.web.FinalServerInitializer;

import java.util.HashMap;


/**
 * @author lingkang
 * Created by 2022/12/6
 * @since 1.0.0
 */
class HandlerHttpRequest extends SimpleChannelInboundHandler<FinalServerContext> {
    private static final Logger log = LoggerFactory.getLogger(HandlerHttpRequest.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FinalServerContext context) throws Exception {
        // log.info(context.getRequest().getHttpMethod().name() + " path=" + context.getRequest().getPath());

        // 在此更新会话访问
        ((HttpSession) FinalServerHttpContext.getRequest().getSession()).updateLastAccessTime();
        try {
            // 过滤器
            new FilterChain(FinalServerInitializer.filters, FinalServerInitializer.requestHandlers).doFilter(context);

            if (context.getResponse().isReady()) {
                HttpResponse res = (HttpResponse) context.getResponse();
                if (res.isStatic()) {
                    HttpHandler.returnStaticFile(res.getFilePath(), ctx, context);
                    return;
                }

                // 添加会话到cookie
                FinalServerConfiguration.httpSessionManage.addSessionIdToCurrentHttp(context);

                // 添加cookie
                HttpUtils.addHeaderCookie(context);

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
        } catch (Exception e) {
            throw e;
        } finally {
            FinalServerHttpContext.remove();
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
