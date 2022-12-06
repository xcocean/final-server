package top.lingkang.finalserver.server.web.handler;

import top.lingkang.finalserver.server.web.http.FinalServerContext;
import top.lingkang.finalserver.server.web.http.HandlerChain;

/**
 * @author lingkang
 * Created by 2022/12/7
 */
public class StaticResourceHandlerChain implements HandlerChain {
    @Override
    public boolean handler(FinalServerContext context) throws Exception {
        String path = context.getRequest().getPath();

        return true;
    }
}
