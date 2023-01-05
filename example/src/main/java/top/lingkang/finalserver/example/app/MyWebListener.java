package top.lingkang.finalserver.example.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import top.lingkang.finalserver.server.core.WebListener;
import top.lingkang.finalserver.server.web.http.FinalServerContext;

/**
 * @author lingkang
 * 2023/1/5
 * 请求处理计时
 **/
@Component
public class MyWebListener implements WebListener {
    private static final ThreadLocal<Long> time = new ThreadLocal<>();
    private static final ThreadLocal<String> path = new ThreadLocal<>();
    private static final Logger log = LoggerFactory.getLogger(MyWebListener.class);

    @Override
    public void before(FinalServerContext context) throws Exception {
        time.set(System.currentTimeMillis());
        path.set(context.getRequest().getPath());
    }

    @Override
    public void after() throws Exception {
        log.info("url={}, 请求耗时：{}ms",
                path.get(),
                System.currentTimeMillis() - time.get()
        );
        time.remove();
        path.remove();
    }
}
