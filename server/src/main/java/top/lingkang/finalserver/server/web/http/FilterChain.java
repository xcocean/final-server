package top.lingkang.finalserver.server.web.http;

import top.lingkang.finalserver.server.web.handler.ControllerHandler;

/**
 * @author lingkang
 * Created by 2022/12/8
 */
public class FilterChain {
    private Filter[] filters;
    private int length = 0, current = 0;
    private ControllerHandler controllerHandler;

    public FilterChain(Filter[] filters, ControllerHandler controllerHandler) {
        this.filters = filters;
        this.controllerHandler = controllerHandler;
        length = filters.length;
    }

    public void doFilter(FinalServerContext context) throws Exception {
        if (current < length) {
            current++;// 自增
            filters[current - 1].doFilter(context, this);
        } else {
            // 在此处调用处理逻辑方法
            controllerHandler.handler(context);
        }
    }
}
