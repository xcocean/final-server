package top.lingkang.finalserver.server.web.security.config;

import top.lingkang.finalserver.server.web.http.Session;
import top.lingkang.finalserver.server.web.security.base.FinalSessionObject;

/**
 * @author lingkang
 * Created by 2022/10/28
 * @since 3.0.0
 */
class FinalSessionObjectServlet implements FinalSessionObject<Session> {

    Session session;

    @Override
    public Object getAttribute(String name) {
        return session.getAttribute(name);
    }

    @Override
    public void init(Session session) {
        this.session = session;
    }

}
