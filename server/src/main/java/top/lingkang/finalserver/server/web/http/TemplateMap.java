package top.lingkang.finalserver.server.web.http;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lingkang
 * 2023/1/16
 * @since 1.0.0
 * 这时一个特殊的 Map ，为了减少不必要的遍历开支：
 * 模板的 map 参数，只在获取值时增加全局变量。使用时需要注意
 * 只作为候选map，只有在输入的map中无法获取到变量时才会从候选的全局变量中查找
 **/
public class TemplateMap extends HashMap<String, Object> implements Map<String, Object> {

    public TemplateMap(Map<String, Object> map) {
        if (map != null)
            putAll(map);
    }

    @Override
    public Object get(Object key) {
        Object o = super.get(key);
        if (o == null)
            return FinalServerContext.getTemplateGlobalMap().get(key);
        return o;
    }

    @Override
    public boolean containsKey(Object key) {
        boolean containsKey = super.containsKey(key);
        if (!containsKey)
            return FinalServerContext.getTemplateGlobalMap().containsKey(key);
        return containsKey;
    }
}
