package top.lingkang.finalserver.server.web.http;

/**
 * @author lingkang
 * Created by 2022/12/6
 * @since 1.0.0
 * 过滤器
 */
public interface Filter {
    void doFilter(FinalServerContext context, FilterChain filterChain) throws Exception;

    void init();

    default void destroy() {
    }
}
