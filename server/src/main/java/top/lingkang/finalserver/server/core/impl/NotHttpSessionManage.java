package top.lingkang.finalserver.server.core.impl;

import top.lingkang.finalserver.server.core.HttpSessionManage;
import top.lingkang.finalserver.server.web.http.*;

/**
 * @author lingkang
 * Created by 2022/12/12
 */
public class NotHttpSessionManage implements HttpSessionManage {
    @Override
    public Session getSession(Request request, Response response) {
        return new HttpSession("NotHttpSessionManage");
    }

    @Override
    public void updateSessionAccessTime(Session session) {

    }

    @Override
    public void addSessionIdToCurrentHttp(FinalServerContext context) {

    }
}
