package top.lingkang.finalserver.server.web.http;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.lingkang.finalserver.server.core.FinalServerConfiguration;
import top.lingkang.finalserver.server.core.HttpParseTemplate;
import top.lingkang.finalserver.server.utils.HttpUtils;
import top.lingkang.finalserver.server.web.FinalServerHttpContext;


/**
 * @author lingkang
 * Created by 2022/12/6
 * @since 1.0.0
 */
public class HandlerHttpRequest extends SimpleChannelInboundHandler<FinalServerContext> {
    private static final Logger log = LoggerFactory.getLogger(HandlerHttpRequest.class);

    private FilterChain filterChain;
    private HttpParseTemplate parseTemplate;

    public HandlerHttpRequest(FilterChain filterChain, HttpParseTemplate parseTemplate) {
        this.filterChain = filterChain;
        this.parseTemplate = parseTemplate;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FinalServerContext context) throws Exception {
        log.info(context.getRequest().getHttpMethod().name() + " path=" + context.getRequest().getPath());
        try {
            filterChain.doFilter(context);

            if (context.getResponse().isReady()) {
                HttpResponse res = (HttpResponse) context.getResponse();
                if (res.isStatic()) {
                    HttpHandler.returnStaticFile(res.getFilePath(), ctx, context);
                    return;
                }

                // 添加会话到cookie
                FinalServerConfiguration.httpSessionManage.addSessionIdToCurrentHttp(context);

                // 添加cookie
                HttpUtils.addHeaderCookie(context.getResponse());

                if (res.isTemplate()) {
                    HttpUtils.sendResponse(
                            ctx,
                            parseTemplate.getTemplate(res.getTemplatePath(), res.getTemplateMap()),
                            res.getHeaders(),
                            200);
                } else
                    HttpUtils.sendResponse(ctx, res, 200);
            } else {// 返回 404
                FinalServerConfiguration.webExceptionHandler.notHandler(ctx);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            // 在此更新会话访问
            FinalServerConfiguration.httpSessionManage.updateSessionAccessTime(FinalServerHttpContext.getRequest().getSession());
            FinalServerHttpContext.remove();
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        FinalServerConfiguration.webExceptionHandler.exception(ctx, cause);
        if (ctx.channel().isActive()) {// 未关闭时手动关闭
            HttpUtils.sendString(ctx, "", 500);
        }
    }
}
