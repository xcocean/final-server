package top.lingkang.finalserver.server.web.security.base;

/**
 * @author lingkang
 * Created by 2022/10/28
 * @since 3.0.0
 */
public abstract class FinalHolder {
    public void login(String username, String[] role) {
    }

    public void logout() {
    }

    public String[] getRole() {
        return new String[0];
    }

    public String getUsername() {
        return null;
    }

    public boolean isLogin() {
        return false;
    }
}
