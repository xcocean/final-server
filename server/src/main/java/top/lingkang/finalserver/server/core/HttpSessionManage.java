package top.lingkang.finalserver.server.core;

import top.lingkang.finalserver.server.annotation.NotNull;
import top.lingkang.finalserver.server.web.http.FinalServerContext;
import top.lingkang.finalserver.server.web.http.Request;
import top.lingkang.finalserver.server.web.http.Session;

import java.util.HashMap;

/**
 * @author lingkang
 * Created by 2022/12/12
 */
public interface HttpSessionManage {
    // 获取会话，cookie中或者请求参数、请求头中没有session时，应该创建，不能返回空值
    // 可以通过 new HttpSession(FinalServerConfiguration.idGenerateFactory.generateHttpId(request)) 返回默认的token
    @NotNull
    Session getSession(Request request);

    // 更新会话的访问时间
    void updateSessionAccessTime(Session session);

    // 获取会话的map属性
    HashMap<String, Object> getSessionAttribute(Request request);

    void addSessionIdToCurrentHttp(FinalServerContext context);

}
