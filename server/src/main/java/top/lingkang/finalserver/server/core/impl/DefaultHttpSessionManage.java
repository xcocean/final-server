package top.lingkang.finalserver.server.core.impl;

import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.lingkang.finalserver.server.FinalServerApplication;
import top.lingkang.finalserver.server.core.FinalServerConfiguration;
import top.lingkang.finalserver.server.core.FinalServerProperties;
import top.lingkang.finalserver.server.core.HttpSessionManage;
import top.lingkang.finalserver.server.core.ShutdownEvent;
import top.lingkang.finalserver.server.web.http.FinalServerContext;
import top.lingkang.finalserver.server.web.http.HttpSession;
import top.lingkang.finalserver.server.web.http.Request;
import top.lingkang.finalserver.server.web.http.Session;

import java.util.*;

/**
 * @author lingkang
 * Created by 2022/12/12
 */
public class DefaultHttpSessionManage implements HttpSessionManage {
    private static final Logger log = LoggerFactory.getLogger(DefaultHttpSessionManage.class);
    private final static HashMap<String, Session> sessionMap = new HashMap<>();
    private Timer timer = new Timer();

    public DefaultHttpSessionManage() {
        FinalServerApplication.addShutdownHook(new ShutdownEvent() {
            @Override
            public void shutdown() throws Exception {
                timer.cancel();
            }
        });

        // 会话淘汰机制
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (sessionMap.isEmpty())
                    return;
                log.info("自动session清理，当前session个数：{}", sessionMap.size());
                // 预留10分钟
                long removeTime = System.currentTimeMillis() - FinalServerProperties.server_session_age * 1000L - 600000L;
                List<String> removeList = new ArrayList<>();
                HashMap<String, Session> temp = new HashMap<>(sessionMap);
                for (Map.Entry<String, Session> entry : temp.entrySet()) {
                    if (entry.getValue().lastAccessTime() < removeTime) {
                        removeList.add(entry.getKey());
                    }
                }
                for (String key : removeList)
                    sessionMap.remove(key);
                log.info("自动session清理完成，清理session个数：{}", removeList.size());
                temp=null;
                removeList=null;
            }
        }, 600000, 1800000);// 启动后10分钟执行一次，之后每30分钟执行一次
    }

    @Override
    public Session getSession(Request request) {
        Cookie cookie = request.getCookie(FinalServerProperties.server_session_name);
        Session session;
        if (cookie == null) {
            session = new HttpSession(FinalServerConfiguration.idGenerateFactory.generateHttpId(request));
            sessionMap.put(session.getId(), session);
            return session;
        }

        session = sessionMap.get(cookie.value());
        if (session == null) {
            session = new HttpSession(FinalServerConfiguration.idGenerateFactory.generateHttpId(request));
            sessionMap.put(session.getId(), session);
        } else if (session.isExpire()) {
            sessionMap.remove(cookie.value());
            session = new HttpSession(FinalServerConfiguration.idGenerateFactory.generateHttpId(request));
            sessionMap.put(session.getId(), session);
        }

        return session;
    }

    @Override
    public void updateSessionAccessTime(Session session) {
        if (session != null)
            ((HttpSession) session).updateLastAccessTime();
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
