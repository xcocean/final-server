package top.lingkang.finalserver.server.web.security.utils;

import org.springframework.util.AntPathMatcher;
import top.lingkang.finalserver.server.web.security.base.FinalAuth;
import top.lingkang.finalserver.server.web.security.constants.FinalConstants;
import top.lingkang.finalserver.server.web.security.error.FinalPermissionException;

import java.util.Arrays;
import java.util.HashSet;

/**
 * @author lingkang
 * date 2022/1/8
 * @since 1.0.0
 */
public class AuthUtils {
    private static final AntPathMatcher matcher = new AntPathMatcher();

    public static boolean matcher(String pattern, String path) {
        return matcher.match(pattern, path);
    }

    /**
     * 入参均不能为空！长度大于 1
     *
     * @param roles not empty
     * @param has   not empty
     */
    public static void checkRole(String[] roles, String[] has) {
        for (String r : roles) {
            for (String h : has) {
                if (r.equals(h))
                    return;
            }
        }
        throw new FinalPermissionException(FinalConstants.UNAUTHORIZED_MSG);
    }

    /**
     * 入参均不能为空！长度大于 1
     *
     * @param roles not empty
     * @param has   not empty
     */
    public static void checkAndRole(String[] roles, String[] has) {
        for (String r : roles) {
            boolean no = true;
            for (String h : has) {
                if (r.equals(h)) {
                    no = false;
                    break;
                }
            }
            if (no) {
                throw new FinalPermissionException(FinalConstants.UNAUTHORIZED_MSG);
            }
        }
    }


    /**
     * 去重处理，多合一，关键
     */
    public static FinalAuth[] AllToOne(FinalAuth[] arr) {
        if (arr.length == 0)// bug fix：防止空检验 2022-12-15
            return new FinalAuth[0];
        FinalAuth finalAuth = new FinalAuth();
        HashSet<String> role = new HashSet<>(), andRole = new HashSet<>();
        for (FinalAuth auth : arr) {
            role.addAll(Arrays.asList(auth.getRole()));
            andRole.addAll(Arrays.asList(auth.getAndRole()));
        }
        finalAuth.setRole(role.toArray(new String[0]));
        finalAuth.setAndRole(andRole.toArray(new String[0]));
        return new FinalAuth[]{finalAuth};
    }

}
