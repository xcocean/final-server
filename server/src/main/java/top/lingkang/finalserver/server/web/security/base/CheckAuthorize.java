package top.lingkang.finalserver.server.web.security.base;

import top.lingkang.finalserver.server.web.security.error.FinalBaseException;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * @author lingkang
 * Created by 2022/3/22
 * 设置授权检查
 * @since 1.0.0
 * properties.checkAuthorize()
 *           .pathMatchers("/user").hasAnyRole("user", "vip1") // 有其中任意角色就能访问
 *           .pathMatchers("/vip/**").hasAllRole("user", "vip1");// 必须同时有所有角色才能访问
 */
public class CheckAuthorize implements Serializable {
    public CheckAuthorize() {
    }

    // 权限校验
    private HashMap<String, FinalAuth> authorize = new HashMap<>();

    private String tempMatchers;
    private FinalAuth tempFinalAuth;

    public CheckAuthorize pathMatchers(String matchers) {
        tempMatchers = matchers;
        if (authorize.get(tempMatchers) == null) {
            tempFinalAuth = new FinalAuth();
        } else {
            tempFinalAuth = authorize.get(tempMatchers);
        }
        return this;
    }

    public CheckAuthorize hasAnyRole(String... anyRole) {
        if (tempFinalAuth == null) {
            throw new FinalBaseException("请先设置匹配路径：pathMatchers");
        }

        Set<String> newRole = new HashSet<>(Arrays.asList(tempFinalAuth.getRole()));
        newRole.addAll(Arrays.asList(anyRole));
        tempFinalAuth.setRole(newRole.toArray(new String[newRole.size()]));
        authorize.put(tempMatchers, tempFinalAuth);
        return this;
    }

    public CheckAuthorize hasAllRole(String... allRole) {
        if (tempFinalAuth == null) {
            throw new FinalBaseException("请先设置匹配路径：pathMatchers");
        }
        Set<String> newRole = new HashSet<>(Arrays.asList(tempFinalAuth.getRole()));
        newRole.addAll(Arrays.asList(allRole));
        tempFinalAuth.setAndRole(newRole.toArray(new String[newRole.size()]));
        authorize.put(tempMatchers, tempFinalAuth);
        return this;
    }

    public CheckAuthorize hasLogin() {
        if (tempFinalAuth == null) {
            throw new FinalBaseException("请先设置匹配路径：pathMatchers");
        }
        authorize.put(tempMatchers, new FinalAuth());
        return this;
    }

    public HashMap<String, FinalAuth> getAuthorize() {
        return authorize;
    }

    @Override
    public String toString() {
        return "CheckAuthorize{" +
                "authorize=" + authorize +
                '}';
    }
}
