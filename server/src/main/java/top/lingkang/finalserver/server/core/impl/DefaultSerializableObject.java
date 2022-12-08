package top.lingkang.finalserver.server.core.impl;

import com.alibaba.fastjson2.JSON;
import top.lingkang.finalserver.server.core.SerializableObject;

import java.nio.charset.StandardCharsets;

/**
 * @author lingkang
 * Created by 2022/12/8
 */
public class DefaultSerializableObject implements SerializableObject {
    @Override
    public byte[] stringTo(String str) {
        return str.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public byte[] jsonTo(Object json) {
        return JSON.toJSONBytes(json);
    }
}
