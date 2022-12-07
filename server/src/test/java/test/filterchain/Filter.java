package test.filterchain;

/**
 * @author lingkang
 * Created by 2022/12/7
 */
public interface Filter {
    void doFilter(Object req,Object res,FilterChain filterChain)throws Exception;
}
