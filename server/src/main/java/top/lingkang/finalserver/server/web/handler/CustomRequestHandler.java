package top.lingkang.finalserver.server.web.handler;

import top.lingkang.finalserver.server.web.http.FinalServerContext;

/**
 * @author lingkang
 * Created by 2022/12/10
 * @since 1.0.0
 */
public interface CustomRequestHandler {
    /**
     * 参数需要通过 context.request() 获取
     * 返回响应通过 context.response()
     */
    void handler(FinalServerContext context);
}
