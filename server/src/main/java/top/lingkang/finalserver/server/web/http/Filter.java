package top.lingkang.finalserver.server.web.http;

/**
 * @author lingkang
 * Created by 2022/12/6
 */
public interface Filter {
    boolean doFilter()throws Exception;
}