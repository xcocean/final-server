package top.lingkang.finalserver.server.web.security.base.impl;


import top.lingkang.finalserver.server.web.security.base.CheckPathCache;
import top.lingkang.finalserver.server.web.security.base.FinalAuth;

import java.util.HashMap;
import java.util.HashSet;

/**
 * @author lingkang
 * Created by 2022/7/1
 * 默认的缓存实现
 * @since 2.0.0
 */
public class DefaultCheckPathCache implements CheckPathCache {
    public HashSet<String> cacheExcludePath = new HashSet<>();
    public HashMap<String, FinalAuth[]> cacheAuths = new HashMap<>();

    @Override
    public HashSet<String> getExcludePath() {
        return cacheExcludePath;
    }

    @Override
    public HashMap<String, FinalAuth[]> getAuths() {
        return cacheAuths;
    }
}
