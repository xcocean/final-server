package top.lingkang.finalserver.server.web.handler;

import top.lingkang.finalserver.server.web.http.FinalServerContext;

/**
 * @author lingkang
 * Created by 2022/12/8
 * @since 1.0.0
 */
public interface RequestHandler {
    /**
     * 返回 true 时将不再处理下面的 RequestHandler
     */
    boolean handler(FinalServerContext context) throws Exception;
}
