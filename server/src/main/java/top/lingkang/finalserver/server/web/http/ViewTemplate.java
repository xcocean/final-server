package top.lingkang.finalserver.server.web.http;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lingkang
 * 2023/1/11
 * @since 1.0.0
 * controller处理返回 ViewTemplate 时，将返回对应的模板渲染
 **/
public class ViewTemplate {
    private String template;
    private Map<String, Object> map = new HashMap<>();

    public ViewTemplate(String template) {
        this.template = template;
    }

    public ViewTemplate(String template, Map<String, Object> map) {
        this.template = template;
        this.map = map;
    }

    /**
     * 添加属性
     */
    public ViewTemplate addAttribute(String name, Object value) {
        map.put(name, value);
        return this;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public Map<String, Object> getMap() {
        return map;
    }

    public void setMap(HashMap<String, Object> map) {
        this.map = map;
    }
}
