package top.lingkang.finalserver.server.core.impl;

import top.lingkang.finalserver.server.core.HttpSessionManage;
import top.lingkang.finalserver.server.web.http.FinalServerContext;
import top.lingkang.finalserver.server.web.http.HttpSession;
import top.lingkang.finalserver.server.web.http.Request;
import top.lingkang.finalserver.server.web.http.Session;

/**
 * @author lingkang
 * Created by 2022/12/12
 * @since 1.0.0
 * 空的会话管理，常用于微服务等无session场景
 */
public class EmptyHttpSessionManage implements HttpSessionManage {
    @Override
    public Session getSession(Request request) {
        return new HttpSession("EmptyHttpSessionManage");
    }

    @Override
    public void bindCurrentSession(FinalServerContext context) {

    }
}
