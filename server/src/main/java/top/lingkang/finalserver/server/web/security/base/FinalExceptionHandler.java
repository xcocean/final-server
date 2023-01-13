package top.lingkang.finalserver.server.web.security.base;

/**
 * @author lingkang
 * Created by 2022/1/7
 * @since 1.0.0
 * 需要注意，
 * request、response 均为 top.lingkang.finalserver.server.web.http.FinalServerContext
 */
public interface FinalExceptionHandler<R,T> {
    /**
     * 需要注意，
     * request、response 均为 top.lingkang.finalserver.server.web.http.FinalServerContext
     */
    void permissionException(Throwable e, R request, T response);

    /**
     * 需要注意，
     * request、response 均为 top.lingkang.finalserver.server.web.http.FinalServerContext
     *
     * @param e
     * @param request
     * @param response
     */
    void notLoginException(Throwable e, R request, T response);

    /**
     * 需要注意，
     * request、response 均为 top.lingkang.finalserver.server.web.http.FinalServerContext
     *
     * @param e
     * @param request
     * @param response
     */
    void exception(Throwable e, R request, T response);
}
