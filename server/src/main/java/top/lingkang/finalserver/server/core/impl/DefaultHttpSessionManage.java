package top.lingkang.finalserver.server.core.impl;

import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.lingkang.finalserver.server.core.FinalServerConfiguration;
import top.lingkang.finalserver.server.core.HttpSessionManage;
import top.lingkang.finalserver.server.web.http.FinalServerContext;
import top.lingkang.finalserver.server.web.http.HttpSession;
import top.lingkang.finalserver.server.web.http.Request;
import top.lingkang.finalserver.server.web.http.Session;

import java.util.*;

/**
 * @author lingkang
 * Created by 2022/12/12
 * @since 1.0.0
 */
public class DefaultHttpSessionManage implements HttpSessionManage {
    protected static final Logger log = LoggerFactory.getLogger(DefaultHttpSessionManage.class);
    protected final static HashMap<String, Session> sessionMap = new HashMap<>();
    protected Timer timer = new Timer();

    public DefaultHttpSessionManage() {

        // 会话淘汰机制
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (sessionMap.isEmpty())
                    return;
                log.info("自动session清理，当前session个数：{}", sessionMap.size());
                // 预留10分钟
                long removeTime = System.currentTimeMillis() - FinalServerConfiguration.sessionExpire - 600000L;
                List<String> removeList = new ArrayList<>();
                HashMap<String, Session> temp = new HashMap<>(sessionMap);
                for (Map.Entry<String, Session> entry : temp.entrySet()) {
                    if (entry.getValue().lastAccessTime() < removeTime) {
                        removeList.add(entry.getKey());
                    }
                }
                for (String key : removeList)
                    sessionMap.remove(key);
                log.info("自动session清理完成，清理session个数：{} ，剩余session个数： {}", removeList.size(), sessionMap.size());
                temp = null;
                removeList = null;
            }
        }, 600000, 1800000);// 启动后10分钟执行一次，之后每30分钟执行一次
    }

    @Override
    public Session getSession(Request request) {
        Cookie cookie = request.getCookie(FinalServerConfiguration.sessionName);
        Session session;
        if (cookie == null) {
            session = new HttpSession(FinalServerConfiguration.idGenerateFactory.generateSessionId(request));
            return session;
        }

        session = sessionMap.get(cookie.value());
        if (session == null) {
            session = new HttpSession(FinalServerConfiguration.idGenerateFactory.generateSessionId(request));
        } else if (session.isExpire()) {
            sessionMap.remove(cookie.value());
            session = new HttpSession(FinalServerConfiguration.idGenerateFactory.generateSessionId(request));
        }

        return session;
    }

    @Override
    public void bindCurrentSession(FinalServerContext context) {
        Session session = context.getRequest().getSession();
        if (session.hasUpdateAttribute()) {
            sessionMap.put(session.getId(), session);
            DefaultCookie cookie = new DefaultCookie(FinalServerConfiguration.sessionName, context.getRequest().getSession().getId());
            cookie.setMaxAge(FinalServerConfiguration.sessionExpire);
            cookie.setPath("/");
            context.getResponse().addCookie(cookie);
        }
        // log.info("当前session数量：{}", sessionMap.size());
    }
}
