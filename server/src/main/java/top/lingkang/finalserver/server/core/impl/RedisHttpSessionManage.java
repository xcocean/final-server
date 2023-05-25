package top.lingkang.finalserver.server.core.impl;

import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import org.redisson.api.RedissonClient;
import top.lingkang.finalserver.server.core.FinalServerConfiguration;
import top.lingkang.finalserver.server.core.FinalServerProperties;
import top.lingkang.finalserver.server.core.HttpSessionManage;
import top.lingkang.finalserver.server.web.http.FinalServerContext;
import top.lingkang.finalserver.server.web.http.HttpSession;
import top.lingkang.finalserver.server.web.http.Request;
import top.lingkang.finalserver.server.web.http.Session;

import java.util.concurrent.TimeUnit;

/**
 * @author lingkang
 * Created by 2022/12/13
 * @since 1.0.0
 * 会话存储的redis实现
 */
public class RedisHttpSessionManage implements HttpSessionManage {
    protected RedissonClient redissonClient;
    protected static final ThreadLocal<Session> localSession = new ThreadLocal<>();

    public RedisHttpSessionManage(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    public Session getSession(Request request) {
        Session get = localSession.get();
        if (get != null)
            return get;
        Cookie cookie = request.getCookie(FinalServerProperties.server_session_name);
        Session session = null;
        if (cookie == null) {
            session = new HttpSession(FinalServerConfiguration.idGenerateFactory.generateSessionId(request));
            return session;
        }
        Object bucket = redissonClient.getBucket(cookie.value()).get();
        if (bucket != null)
            session = (Session) bucket;
        if (session == null) {
            session = new HttpSession(FinalServerConfiguration.idGenerateFactory.generateSessionId(request));
        } else if (session.isExpire()) {
            redissonClient.getBucket(session.getId()).delete();
            session = new HttpSession(FinalServerConfiguration.idGenerateFactory.generateSessionId(request));
        }
        localSession.set(session);
        return session;
    }

    private void setSession(Session session) {
        // 添加100毫秒，防止临界值
        redissonClient.getBucket(session.getId()).set(session, FinalServerProperties.server_session_age + 100, TimeUnit.MILLISECONDS);
    }

    @Override
    public void bindCurrentSession(FinalServerContext context) {
        if (context.getRequest().getSession().hasUpdateAttribute()) {
            DefaultCookie cookie = new DefaultCookie(FinalServerProperties.server_session_name, context.getRequest().getSession().getId());
            cookie.setMaxAge(FinalServerProperties.server_session_age);
            context.getResponse().addCookie(cookie);
            setSession(context.getRequest().getSession());
        }
        localSession.remove();
    }
}
