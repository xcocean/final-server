package top.lingkang.finalserver.server.web.http;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.QueryStringDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.lingkang.finalserver.server.core.FinalServerConfiguration;
import top.lingkang.finalserver.server.error.FinalServerException;
import top.lingkang.finalserver.server.utils.BeanUtils;
import top.lingkang.finalserver.server.utils.CommonUtils;
import top.lingkang.finalserver.server.web.FinalServerInitializer;


/**
 * @author lingkang
 * Created by 2022/12/6
 * @since 1.0.0
 */
class HandlerHttpRequest extends SimpleChannelInboundHandler<FinalServerContext> {
    private static final Logger log = LoggerFactory.getLogger(HandlerHttpRequest.class);
    private FinalServerContext context;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FinalServerContext context) throws Exception {
        this.context = context;
        try {
            CommonUtils.pushWebListenerBefore(context);
            Request request = context.getRequest();
            // 在此更新会话访问
            request.getSession().updateLastAccessTime();
            // 过滤器、处理链
            new FilterChain(FinalServerInitializer.filters, FinalServerInitializer.requestHandlers).doFilter(context);
            Response response = context.getResponse();
            if (response.isReady()) {
                if (response.getResponseFile() != null) {
                    FinalServerConfiguration.returnStaticFileHandler
                            .returnStaticFile(context, ctx);
                    return;
                }

                if (response.isTemplate()) {
                    HttpUtils.sendTemplate(
                            ctx,
                            FinalServerConfiguration.httpParseTemplate.getTemplate(
                                    response.getTemplatePath(),
                                    HttpUtils.getReturnFinalTemplateMap(context), FinalServerContext.templateGlobalMap, context),
                            200);
                } else { // 其他内容
                    if (response.getForwardPath() != null) {// 是否使用了转发
                        if (request.getPath().equals(response.getForwardPath())) {
                            throw new FinalServerException("006请求路径不能与转发路径相同！请求路径：" + request.getPath() + "  转发路径：" + request.getPath());
                        }
                        request.getFullHttpRequest().setUri(response.getForwardPath());
                        // 修改转发参数初始化 // 追加转发参数
                        QueryStringDecoder queryUri = (QueryStringDecoder) BeanUtils.getAttributeValue(request, "queryUri");
                        BeanUtils.updateAttributeValue(queryUri, "path", response.getForwardPath());
                        BeanUtils.updateAttributeValue(request, "queryUri", queryUri);
                        BeanUtils.updateAttributeValue(response, "isReady", false);
                        // 过滤器、处理链
                        new FilterChain(FinalServerInitializer.filters, FinalServerInitializer.requestHandlers).doFilter(context);
                    }
                    HttpUtils.sendResponse(ctx, response, response.getStatusCode());
                }
            } else {// 返回 404
                FinalServerConfiguration.webExceptionHandler.notHandler(ctx);
            }
        } catch (Exception e) {
            FinalServerConfiguration.webExceptionHandler.exception(ctx, e);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // http处理后仍出现异常
        log.error("http异常处理后仍出现异常，请检查异常处理是否正确：", cause);
        if (ctx.channel().isActive()) {// 未关闭时手动关闭
            HttpUtils.sendString(ctx, "服务错误", 500);
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
