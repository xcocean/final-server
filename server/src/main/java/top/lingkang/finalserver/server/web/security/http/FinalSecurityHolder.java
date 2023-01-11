package top.lingkang.finalserver.server.web.security.http;


import top.lingkang.finalserver.server.web.http.FinalServerContext;
import top.lingkang.finalserver.server.web.http.Session;
import top.lingkang.finalserver.server.web.security.base.FinalHolder;
import top.lingkang.finalserver.server.web.security.constants.FinalSessionKey;

/**
 * @author lingkang
 * Created by 2022/1/7
 * FinalSecurity 开放方法（核心）
 * @since 1.0.0
 */
public class FinalSecurityHolder extends FinalHolder {
    @Override
    public void login(String username, String[] role) {
        Session session = FinalServerContext.currentContext().getRequest().getSession();
        session.setAttribute(FinalSessionKey.LOGIN_USERNAME, username);
        session.setAttribute(FinalSessionKey.HAS_ROLE, role);
        session.setAttribute(FinalSessionKey.IS_LOGIN, true);
    }

    @Override
    public void logout() {
        Session session = FinalServerContext.currentContext().getRequest().getSession();
        session.removeAttribute(FinalSessionKey.IS_LOGIN);
        session.removeAttribute(FinalSessionKey.HAS_ROLE);
        session.removeAttribute(FinalSessionKey.LOGIN_USERNAME);
    }

    @Override
    public String[] getRole() {
        Object finalRole = FinalServerContext.currentContext().getRequest().getSession().getAttribute(FinalSessionKey.HAS_ROLE);
        if (finalRole != null) {
            return (String[]) finalRole;
        }
        return null;
    }

    @Override
    public String getUsername() {
        Object username = FinalServerContext.currentContext().getRequest().getSession().getAttribute(FinalSessionKey.LOGIN_USERNAME);
        if (username != null) {
            return (String) username;
        }
        return null;
    }

    @Override
    public boolean isLogin() {
        Object login = FinalServerContext.currentContext().getRequest().getSession().getAttribute(FinalSessionKey.IS_LOGIN);
        if (login != null)
            return (boolean) login;
        return false;
    }
}
