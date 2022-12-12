package top.lingkang.finalserver.server.core;

import top.lingkang.finalserver.server.web.http.FinalServerContext;
import top.lingkang.finalserver.server.web.http.Request;
import top.lingkang.finalserver.server.web.http.Response;
import top.lingkang.finalserver.server.web.http.Session;

/**
 * @author lingkang
 * Created by 2022/12/12
 */
public interface HttpSessionManage {
    Session getSession(Request request, Response response);

    void updateSessionAccessTime(Session session);

    void addSessionIdToCurrentHttp(FinalServerContext context);

}
