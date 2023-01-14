package top.lingkang.finalserver.server.core.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import top.lingkang.finalserver.server.core.SerializableObject;

import java.nio.charset.StandardCharsets;

/**
 * @author lingkang
 * Created by 2022/12/8
 * @since 1.0.0
 */
public class DefaultSerializableObject implements SerializableObject {
    @Override
    public byte[] stringTo(String str) {
        return str.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * 格式化输出，保留空值
     */
    @Override
    public byte[] jsonTo(Object json) {
        return JSON.toJSONBytes(json, JSONWriter.Feature.WriteMapNullValue);
    }
}
