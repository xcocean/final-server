package top.lingkang.finalserver.server.web.http;

/**
 * @author lingkang
 * Created by 2022/12/6
 * 过滤器
 */
public interface Filter {
    Filter doFilter(FinalServerContext context, Filter chain)throws Exception;


}
