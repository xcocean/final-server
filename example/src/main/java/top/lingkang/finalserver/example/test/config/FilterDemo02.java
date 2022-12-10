package top.lingkang.finalserver.example.test.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import top.lingkang.finalserver.server.web.http.Filter;
import top.lingkang.finalserver.server.web.http.FilterChain;
import top.lingkang.finalserver.server.web.http.FinalServerContext;

/**
 * @author lingkang
 * Created by 2022/12/6
 */
@Order(66)
// @Configuration
public class FilterDemo02 implements Filter {

    @Override
    public void doFilter(FinalServerContext context, FilterChain filterChain) throws Exception {
        System.out.println("FilterDemo02");
        filterChain.doFilter(context);
    }
}

