package top.lingkang.finalserver.server.web.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.lingkang.finalserver.server.web.http.Filter;

/**
 * @author lingkang
 * Created by 2022/12/7
 */
public class FilterHandlerChain   {
    private static final Logger log= LoggerFactory.getLogger(FilterHandlerChain.class);
    private ControllerHandler controllerHandler;

    private Filter[] filters;

    public FilterHandlerChain(Filter[] filters) {
        this.filters = filters;
        // log.debug("过滤器顺序："+ Arrays.toString(filters));
    }

    public Filter[] getFilters() {
        return filters;
    }
}
