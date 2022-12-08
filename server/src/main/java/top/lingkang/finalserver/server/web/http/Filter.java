package top.lingkang.finalserver.server.web.http;

import top.lingkang.finalserver.server.web.http.FilterChain;

/**
 * @author lingkang
 * Created by 2022/12/6
 * 过滤器
 */
public interface Filter {
    void doFilter(FinalServerContext context, FilterChain filterChain)throws Exception;


}
