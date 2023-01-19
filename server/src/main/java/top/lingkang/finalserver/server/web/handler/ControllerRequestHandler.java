package top.lingkang.finalserver.server.web.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.lingkang.finalserver.server.utils.MatchUtils;
import top.lingkang.finalserver.server.utils.TypeUtils;
import top.lingkang.finalserver.server.web.entity.RequestInfo;
import top.lingkang.finalserver.server.web.http.FinalServerContext;
import top.lingkang.finalserver.server.web.http.ViewTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lingkang
 * Created by 2022/12/7
 * @since 1.0.0
 */
public class ControllerRequestHandler extends BuildControllerHandler implements RequestHandler {
    private static final Logger log = LoggerFactory.getLogger(ControllerRequestHandler.class);
    public static Map<String, Object> cacheControllerBean = new HashMap<>();

    public boolean handler(FinalServerContext context) throws Exception {
        String reqURL = context.getRequest().getHttpMethod().name() + "_" + context.getRequest().getPath();
        RequestInfo requestInfo = absolutePath.get(reqURL);
        Map<String, String> matcherRestFul = null;
        if (requestInfo == null) {// 绝对路径匹配为空时，匹配 rest ful
            for (RequestInfo info : restFulPath) {
                if (info.getRequestMethod().equals(context.getRequest().getHttpMethod().name())) {
                    matcherRestFul = MatchUtils.matcherRestFul(info.getPath(), context.getRequest().getPath(), info.getRestFulParam());
                    if (matcherRestFul != null) {
                        requestInfo = info;
                        break;
                    }
                }
            }
        }
        if (requestInfo != null) {
            Object result = null;
            if (requestInfo.isCustomRequestHandler()) {// 自定义的请求处理
                requestInfo.getCustomRequestHandler().handler(context);
                return true;
            }

            // 缓存
            Object controllerBean = cacheControllerBean.get(requestInfo.getBeanName());
            if (controllerBean == null) {
                // 没有时从上下文中获取
                controllerBean = applicationContext.getBean(requestInfo.getBeanName(), requestInfo.getControllerClass());
                cacheControllerBean.put(requestInfo.getBeanName(), controllerBean);
            }

            // 绝对路径请求、 rest ful 请求、 aop赋值
            result = requestInfo.getMethod().invoke(controllerBean, new Object[requestInfo.getParamNum()]);

            // 结果处理 ----------------------------------------------------------------------------------------------
            if (context.getResponse().isReady())
                return true;

            // 无模板，返回值时
            if (result == null) {
                // 返回空时，直接输出空字符串
                context.getResponse().returnString("");
            } else if (result instanceof ViewTemplate) {// 返回视图模板时
                ViewTemplate template = (ViewTemplate) result;
                if (context.getResponse().getTemplateMap() == null) {
                    context.getResponse().returnTemplate(template.getTemplate(), template.getMap());
                } else {
                    context.getResponse().getTemplateMap().putAll(template.getMap());
                    context.getResponse().returnTemplate(template.getTemplate());
                }
            } else {
                if (TypeUtils.isBaseType(result.getClass())) {
                    // 基础类型
                    context.getResponse().returnString(result.toString());
                } else {
                    // json
                    context.getResponse().returnJsonObject(result);
                }
            }
        }
        return true;
    }
}
