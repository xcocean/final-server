package top.lingkang.finalserver.server.core.impl;

import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import top.lingkang.finalserver.server.core.FinalServerConfiguration;
import top.lingkang.finalserver.server.core.FinalServerProperties;
import top.lingkang.finalserver.server.core.HttpSessionManage;
import top.lingkang.finalserver.server.web.http.*;

import java.util.HashMap;

/**
 * @author lingkang
 * Created by 2022/12/12
 */
public class DefaultHttpSessionManage implements HttpSessionManage {
    private final static HashMap<String, Session> sessionMap = new HashMap<>();

    @Override
    public Session getSession(Request request) {
        Cookie cookie = request.getCookie(FinalServerProperties.server_session_name);
        Session session;
        if (cookie == null) {
            session = new HttpSession(FinalServerConfiguration.serverGenerateId.generateHttpId(request));
            sessionMap.put(session.getId(), session);
            return session;
        }

        session = sessionMap.get(cookie.value());
        if (session == null) {
            session = new HttpSession(FinalServerConfiguration.serverGenerateId.generateHttpId(request));
            sessionMap.put(session.getId(), session);
        } else if (session.isExpire()) {
            sessionMap.remove(cookie.value());
            session = new HttpSession(FinalServerConfiguration.serverGenerateId.generateHttpId(request));
            sessionMap.put(session.getId(), session);
        }

        return session;
    }

    @Override
    public void updateSessionAccessTime(Session session) {
        if (session != null)
            ((HttpSession) session).access();
    }

    @Override
    public HashMap<String, Object> getSessionAttribute(Request request) {
        return request.getSession().getAttributeMap();
    }

    @Override
    public void addSessionIdToCurrentHttp(FinalServerContext context) {
        if (context.getRequest().getSession().hasAttribute() && !context.getRequest().getSession().isExpire()) {
            DefaultCookie cookie = new DefaultCookie(FinalServerProperties.server_session_name, context.getRequest().getSession().getId());
            cookie.setMaxAge(FinalServerProperties.server_session_age);
            context.getResponse().addCookie(cookie);
        }
    }
}
