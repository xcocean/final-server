package top.lingkang.finalserver.server.core;

import top.lingkang.finalserver.server.web.http.HttpRequest;
import top.lingkang.finalserver.server.web.http.Request;

/**
 * @author lingkang
 * Created by 2022/12/11
 * 默认的id生成器
 */
public interface ServerGenerateId {
    String generateNettyId();

    String generateHttpId(Request httpRequest);
}
