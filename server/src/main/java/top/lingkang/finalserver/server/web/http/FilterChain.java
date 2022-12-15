package top.lingkang.finalserver.server.web.http;

import top.lingkang.finalserver.server.web.handler.RequestHandler;

/**
 * @author lingkang
 * Created by 2022/12/8
 * @since 1.0.0
 */
public class FilterChain {
    private Filter[] filters;
    private int length, current = 0;
    private RequestHandler[] requestHandler;

    public FilterChain(Filter[] filters, RequestHandler[] requestHandler) {
        this.filters = filters;
        this.requestHandler = requestHandler;
        length = filters.length;
    }

    public void doFilter(FinalServerContext context) throws Exception {
        if (current < length) {
            current++;// 自增
            filters[current - 1].doFilter(context, this);
        } else {
            // 在此处调用处理逻辑方法
            for (RequestHandler handler : requestHandler) {
                if (handler.handler(context)) break;
            }
        }
    }

    public Filter[] getFilters() {
        return filters;
    }

    public RequestHandler[] getRequestHandler() {
        return requestHandler;
    }
}
