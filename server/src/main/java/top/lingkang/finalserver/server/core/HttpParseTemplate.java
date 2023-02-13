package top.lingkang.finalserver.server.core;

import top.lingkang.finalserver.server.web.http.FinalServerContext;

import java.util.Map;

/**
 * @author lingkang
 * Created by 2022/12/11
 * @since 1.0.0
 * 模板解析接口，默认使用 freemarker
 * 默认实现 {@link top.lingkang.finalserver.server.core.impl.DefaultHttpParseTemplate}
 */
public interface HttpParseTemplate {
    // 初始化模板，每次启动只会执行一次
    void init(String templatePath);

    /**
     *
     * @param template 模板名称
     * @param map 本次渲染变量
     * @param globalMap 全局渲染变量
     * @param context 上下文
     * @return 输出模板
     * @throws Exception
     */
    byte[] getTemplate(String template, Map<String, Object> map, Map<String, Object> globalMap, FinalServerContext context) throws Exception;
}
