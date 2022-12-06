package top.lingkang.finalserver.server.web.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.lingkang.finalserver.server.web.http.Filter;
import top.lingkang.finalserver.server.web.http.FinalServerContext;
import top.lingkang.finalserver.server.web.http.HandlerChain;

import java.util.Arrays;

/**
 * @author lingkang
 * Created by 2022/12/7
 */
public class FilterHandlerChain implements HandlerChain {
    private static final Logger log= LoggerFactory.getLogger(FilterHandlerChain.class);

    private Filter[] filters;

    public FilterHandlerChain(Filter[] filters) {
        this.filters = filters;
        log.info("过滤器顺序："+ Arrays.toString(filters));
    }

    @Override
    public boolean handler(FinalServerContext context) throws Exception {
        for (Filter filter:filters){
            if (!filter.doFilter())
                return false;
        }
        return true;
    }
}
