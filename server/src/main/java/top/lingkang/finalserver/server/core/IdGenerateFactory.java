package top.lingkang.finalserver.server.core;

import top.lingkang.finalserver.server.web.http.Request;

/**
 * @author lingkang
 * Created by 2022/12/11
 * 默认的id生成器
 */
public interface IdGenerateFactory {
    /**
     * 生成netty的ID
     * @return
     */
    String generateNettyId();

    /**
     * 生成 session ID
     * @param httpRequest
     * @return
     */
    String generateSessionId(Request httpRequest);
}
