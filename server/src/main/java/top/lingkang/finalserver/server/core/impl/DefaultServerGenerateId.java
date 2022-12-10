package top.lingkang.finalserver.server.core.impl;

import cn.hutool.core.util.IdUtil;
import top.lingkang.finalserver.server.core.ServerGenerateId;

/**
 * @author lingkang
 * Created by 2022/12/11
 */
public class DefaultServerGenerateId implements ServerGenerateId {
    @Override
    public String generateId() {
        return IdUtil.objectId();
    }
}
