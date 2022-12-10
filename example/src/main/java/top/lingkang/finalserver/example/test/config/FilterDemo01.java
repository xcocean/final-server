package top.lingkang.finalserver.example.test.config;

import cn.hutool.core.thread.ThreadUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import top.lingkang.finalserver.server.web.http.Filter;
import top.lingkang.finalserver.server.web.http.FilterChain;
import top.lingkang.finalserver.server.web.http.FinalServerContext;

/**
 * @author lingkang
 * Created by 2022/12/6
 */
@Order(-1)// 过滤器顺序，越小越优先
// @Configuration
public class FilterDemo01 implements Filter {

    @Override
    public void doFilter(FinalServerContext context, FilterChain filterChain) throws Exception {
        System.out.println("FilterDemo01 过滤前");
        filterChain.doFilter(context);
        System.out.println("FilterDemo01 过滤后");
    }
}
