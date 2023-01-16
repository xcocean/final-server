package top.lingkang.finalserver.server.web.http;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lingkang
 * 2023/1/16
 * @since 1.0.0
 **/
public class TemplateMap extends HashMap<String, Object> implements Map<String, Object> {
    public TemplateMap add(String key, Object value) {
        put(key, value);
        return this;
    }

    public TemplateMap addAll(Map<String, Object> map) {
        putAll(map);
        return this;
    }
}
