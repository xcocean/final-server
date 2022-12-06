package top.lingkang.finalserver.server.web.http;

import top.lingkang.finalserver.server.web.http.FinalServerContext;

/**
 * @author lingkang
 * Created by 2022/12/7
 */
public interface HandlerChain {
    boolean handler(FinalServerContext context)throws Exception;
}
