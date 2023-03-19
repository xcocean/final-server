package top.lingkang.finalserver.server.web.http;

/**
 * @author lingkang
 * Created by 2022/12/6
 * @since 1.0.0
 * 过滤器
 */
public interface Filter {
    /**
     * 过滤器：filterChain.doFilter(context);
     * @param context
     * @param filterChain
     */
    void doFilter(FinalServerContext context, FilterChain filterChain) throws Exception;

    /**
     * 过滤器初始化时会执行
     */
    void init();

    /**
     * 正常停止会执行，多数情况不会执行，不可靠
     */
    default void destroy() {
    }
}
