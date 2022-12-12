package top.lingkang.finalserver.server.core.impl;

import cn.hutool.core.util.IdUtil;
import top.lingkang.finalserver.server.core.ServerGenerateId;
import top.lingkang.finalserver.server.web.http.HttpRequest;
import top.lingkang.finalserver.server.web.http.Request;

/**
 * @author lingkang
 * Created by 2022/12/11
 */
public class DefaultServerGenerateId implements ServerGenerateId {
    @Override
    public String generateNettyId() {
        return IdUtil.objectId();
    }

    @Override
    public String generateHttpId(Request request) {
        return IdUtil.fastUUID();
    }
}
