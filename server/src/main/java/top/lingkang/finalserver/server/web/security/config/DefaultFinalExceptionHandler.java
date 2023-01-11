package top.lingkang.finalserver.server.web.security.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.lingkang.finalserver.server.web.http.FinalServerContext;
import top.lingkang.finalserver.server.web.http.Request;
import top.lingkang.finalserver.server.web.http.Response;
import top.lingkang.finalserver.server.web.security.base.FinalExceptionHandler;

/**
 * @author lingkang
 * Created by 2022/1/7
 * @since 1.0.0
 */
public class DefaultFinalExceptionHandler implements FinalExceptionHandler<FinalServerContext, FinalServerContext> {
    private static final Logger log = LoggerFactory.getLogger(DefaultFinalExceptionHandler.class);

    @Override
    public void permissionException(Throwable e, FinalServerContext request, FinalServerContext response) {
        printError(e, request.getRequest(), response.getResponse(), 403);
    }

    @Override
    public void notLoginException(Throwable e, FinalServerContext request, FinalServerContext response) {
        printError(e, request.getRequest(), response.getResponse(), 400);
    }

    @Override
    public void exception(Throwable e, FinalServerContext request, FinalServerContext response) {
        e.printStackTrace();
        response.getResponse().returnString(e.getMessage());
    }

    private void printError(Throwable e, Request request, Response response, int code) {
        log.warn(e.getMessage() + "  url=" + request.getPath());
        response.setStatusCode(200);
        response.returnString(e.getMessage());
    }
}
