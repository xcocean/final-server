package top.lingkang.finalserver.server.web.http;

/**
 * @author lingkang
 * Created by 2022/12/7
 */
public enum RequestMethod {
    GET,
    HEAD,
    POST,
    PUT,
    PATCH,
    DELETE,
    OPTIONS,
    TRACE;
    private RequestMethod() {
    }
}
