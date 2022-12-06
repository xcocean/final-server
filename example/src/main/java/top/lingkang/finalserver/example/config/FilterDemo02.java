package top.lingkang.finalserver.example.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import top.lingkang.finalserver.server.web.http.Filter;

/**
 * @author lingkang
 * Created by 2022/12/6
 */
@Order(66)
@Configuration
public class FilterDemo02 implements Filter {
    @Override
    public boolean doFilter() throws Exception {
        return false;
    }
}
