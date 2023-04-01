package top.lingkang.finalserver.server.web.http;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lingkang
 * Created by 2022/12/6
 * @since 1.0.0
 * 上下文会再连接断开时移除
 */
public class FinalServerContext {
    public FinalServerContext(Request request) {
        this.request = request;
        finalServerContext.set(this);
    }

    public static final ThreadLocal<FinalServerContext> finalServerContext = new ThreadLocal<>();
    public static final Map<String, Object> templateGlobalMap = new HashMap<>();
    private Request request;
    private Response response;

    public Request getRequest() {
        return request;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    /**
     * 模板渲染全局变量
     * 获取 template自带Map，每次渲染都会将此map数据添加到渲染中，全局静态变量。
     * 若对齐进行赋值，无法覆盖：session request 专有变量
     * 只有在输入的map中无法获取到变量时才会从候选的全局变量中查找
     */
    public static Map<String, Object> getTemplateGlobalMap() {
        return templateGlobalMap;
    }

    // 获取当前的上下文
    public static FinalServerContext currentContext() {
        return finalServerContext.get();
    }

    // 移除当前的上下文，框架会自动处理
    public static void removeCurrentContext() {
        finalServerContext.remove();
    }
}
