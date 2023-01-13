package top.lingkang.finalserver.server.web.security.base;

import top.lingkang.finalserver.server.web.security.base.impl.DefaultCheckPathCache;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author lingkang
 * Created by 2022/1/7
 * @since 1.0.0
 */
public class FinalHttpProperties {
    // 排除路径
    private String[] excludePath = {};

    // 权限校验
    private CheckAuthorize checkAuthorize = new CheckAuthorize();

    private FinalExceptionHandler exceptionHandler;

    // 路径鉴权缓存
    private CheckPathCache checkPathCache = new DefaultCheckPathCache();

    public CheckPathCache getCheckPathCache() {
        return checkPathCache;
    }

    public FinalHttpProperties setCheckPathCache(CheckPathCache checkPathCache) {
        this.checkPathCache = checkPathCache;
        return this;
    }

    public FinalExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }

    public FinalHttpProperties setExceptionHandler(FinalExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
        return this;
    }

    public String[] getExcludePath() {
        return excludePath;
    }

    public FinalHttpProperties setExcludePath(String... excludePath) {
        Set<String> ex = new HashSet<>(Arrays.asList(excludePath));
        this.excludePath = ex.toArray(new String[ex.size()]);
        return this;
    }

    public CheckAuthorize checkAuthorize() {
        return checkAuthorize;
    }

    public CheckAuthorize getCheckAuthorize() {
        return checkAuthorize;
    }
}
