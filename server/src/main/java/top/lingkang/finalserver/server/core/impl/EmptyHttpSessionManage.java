package top.lingkang.finalserver.server.core.impl;

import top.lingkang.finalserver.server.core.HttpSessionManage;
import top.lingkang.finalserver.server.web.http.*;

import java.util.HashMap;

/**
 * @author lingkang
 * Created by 2022/12/12
 */
public class EmptyHttpSessionManage implements HttpSessionManage {
    @Override
    public Session getSession(Request request) {
        return new HttpSession("EmptyHttpSessionManage");
    }

    @Override
    public void updateSessionAccessTime(Session session) {

    }

    @Override
    public HashMap<String, Object> getSessionAttribute(Request request) {
        return null;
    }

    @Override
    public void addSessionIdToCurrentHttp(FinalServerContext context) {

    }
}
