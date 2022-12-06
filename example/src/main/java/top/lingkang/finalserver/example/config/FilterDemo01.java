package top.lingkang.finalserver.example.config;

import org.springframework.context.annotation.Configuration;
import top.lingkang.finalserver.server.web.http.Filter;

/**
 * @author lingkang
 * Created by 2022/12/6
 */
@Configuration
public class FilterDemo01 implements Filter {
    @Override
    public boolean doFilter() throws Exception {
        return false;
    }
}
