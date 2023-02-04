package top.lingkang.finalserver.server.core;

import top.lingkang.finalserver.server.annotation.NotNull;
import top.lingkang.finalserver.server.web.http.FinalServerContext;
import top.lingkang.finalserver.server.web.http.Request;
import top.lingkang.finalserver.server.web.http.Session;

/**
 * @author lingkang
 * Created by 2022/12/12
 * @since 1.0.0
 */
public interface HttpSessionManage {
    // 获取会话，cookie中或者请求参数、请求头中没有session时，应该创建，不能返回空值
    // 可以通过 new HttpSession(FinalServerConfiguration.idGenerateFactory.generateHttpId(request)) 返回默认的token
    @NotNull
    Session getSession(Request request);

    /**
     * 绑定当前会话，默认将它添加到 cookie
     */
    void bindCurrentSession(FinalServerContext context);
}
