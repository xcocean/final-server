package top.lingkang.finalserver.server.web.security.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import top.lingkang.finalserver.server.web.http.Filter;
import top.lingkang.finalserver.server.web.http.FilterChain;
import top.lingkang.finalserver.server.web.http.FinalServerContext;
import top.lingkang.finalserver.server.web.security.base.FinalAuth;
import top.lingkang.finalserver.server.web.security.base.FinalHttpProperties;
import top.lingkang.finalserver.server.web.security.base.FinalSessionObject;
import top.lingkang.finalserver.server.web.security.error.FinalBaseException;
import top.lingkang.finalserver.server.web.security.error.FinalNotLoginException;
import top.lingkang.finalserver.server.web.security.error.FinalPermissionException;
import top.lingkang.finalserver.server.web.security.http.FinalSecurityHolder;
import top.lingkang.finalserver.server.web.security.utils.AuthUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lingkang
 * Created by 2022/2/11
 * 过滤配置类（核心）
 * 匹配优先级别：排除 > 鉴权
 * @since 1.0.0
 */
public class FinalSecurityConfiguration implements Filter {
    private FinalHttpProperties properties = new FinalHttpProperties();
    private final FinalSessionObject sessionObject = new FinalSessionObjectServlet();
    private Logger log= LoggerFactory.getLogger(FinalSecurityConfiguration.class);

    @Override
    public void doFilter(FinalServerContext context, FilterChain filterChain) throws Exception {
        try {
            String path = context.getRequest().getPath();
            // 缓存相关
            if (properties.getCheckPathCache().getExcludePath().contains(path)) {
                filterChain.doFilter(context);
                return;
            } else if (properties.getCheckPathCache().getAuths().containsKey(path)) {
                sessionObject.init(context.getRequest().getSession());
                FinalAuth[] finalAuths = properties.getCheckPathCache().getAuths().get(path);
                for (FinalAuth auth : finalAuths) {
                    auth.check(sessionObject);
                }

                filterChain.doFilter(context);
                return;
            }

            // 排除
            for (String url : properties.getExcludePath()) {
                if (AuthUtils.matcher(url, path)) {
                    properties.getCheckPathCache().getExcludePath().add(path);// 添加缓存
                    filterChain.doFilter(context);
                    return;
                }
            }

            // 检查角色
            HashMap<String, FinalAuth> checkAuth = properties.getCheckAuthorize().getAuthorize();
            List<FinalAuth> auths = new ArrayList<>();
            for (Map.Entry<String, FinalAuth> entry : checkAuth.entrySet()) {
                if (AuthUtils.matcher(entry.getKey(), path)) {
                    auths.add(entry.getValue());
                }
            }

            // cache
            properties.getCheckPathCache().getAuths().put(path, AuthUtils.AllToOne(auths.toArray(new FinalAuth[auths.size()])));

            // 执行检查
            sessionObject.init(context.getRequest().getSession());
            for (FinalAuth auth : auths) {
                auth.check(sessionObject);
            }

            //放行
            filterChain.doFilter(context);
        } catch (Exception e) {
            if (FinalBaseException.class.isAssignableFrom(e.getClass()) || FinalBaseException.class.isAssignableFrom(e.getCause().getClass())) {
                if (e instanceof FinalPermissionException) {
                    properties.getExceptionHandler().permissionException(e, context, context);
                } else if (e.getCause() instanceof FinalPermissionException)
                    properties.getExceptionHandler().permissionException(e.getCause(), context, context);
                else if (e instanceof FinalNotLoginException) {
                    properties.getExceptionHandler().notLoginException(e, context, context);
                } else if (e.getCause() instanceof FinalNotLoginException)
                    properties.getExceptionHandler().notLoginException(e.getCause(), context, context);
                else
                    properties.getExceptionHandler().exception(e, context, context);
            } else {
                throw e;
            }
        }
    }

    @Override
    public void init() {
        if (properties.getExceptionHandler() == null)
            properties.setExceptionHandler(new DefaultFinalExceptionHandler());

        config(properties);
    }

    @Override
    public void destroy() {

    }

    protected void config(FinalHttpProperties properties) {
        this.properties = properties;
    }

    public FinalHttpProperties getProperties() {
        return properties;
    }

    @Bean
    public FinalSecurityHolder finalSecurityHolder(){
        log.debug("final-security 添加上下文处理： FinalSecurityHolder");
        return new FinalSecurityHolder();
    }
}
