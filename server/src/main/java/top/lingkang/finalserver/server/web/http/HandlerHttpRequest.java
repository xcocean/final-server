package top.lingkang.finalserver.server.web.http;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.lingkang.finalserver.server.core.FinalServerConfiguration;
import top.lingkang.finalserver.server.core.HttpParseTemplate;
import top.lingkang.finalserver.server.utils.HttpUtils;
import top.lingkang.finalserver.server.utils.NetUtils;


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
        System.out.println(context.getRequest().getHttpMethod().name() + " path=" + context.getRequest().getPath());

        filterChain.doFilter(context);

        // 添加cookie
        HttpUtils.addHeaderCookie(context.getResponse());

        if (context.getResponse().isReady()) {
            HttpResponse res = (HttpResponse) context.getResponse();
            if (res.isStatic()) {
                HttpHandler.returnStaticFile(res.getFilePath(), ctx, context);
            } else if (res.isTemplate()) {
                HttpUtils.sendResponse(
                        ctx,
                        parseTemplate.getTemplate(res.getTemplatePath(), res.getTemplateMap()),
                        res.getHeaders(),
                        200);
            } else
                HttpUtils.sendResponse(ctx, res, 200);
        } else {// 返回空值
            FinalServerConfiguration.webExceptionHandler.notHandler(ctx);
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
            NetUtils.send(ctx, "", 500);
        }
    }
}
