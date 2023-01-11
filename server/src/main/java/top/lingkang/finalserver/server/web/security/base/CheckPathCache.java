package top.lingkang.finalserver.server.web.security.base;

import java.util.HashMap;
import java.util.HashSet;

/**
 * @author lingkang
 * Created by 2022/7/1
 * 鉴权缓存
 * @since 2.0.0
 */
public interface CheckPathCache {
    HashSet<String> getExcludePath();

    HashMap<String, FinalAuth[]> getAuths();
}
