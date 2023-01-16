package top.lingkang.finalserver.server.web.http;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lingkang
 * 2023/1/16
 * @since 1.0.0
 * put 已经被重新，无法返回旧value
 **/
public class TemplateMap extends HashMap<String, Object> implements Map<String, Object> {
    @Override
    public TemplateMap put(String key, Object value) {
        super.put(key, value);
        return this;
    }
}
