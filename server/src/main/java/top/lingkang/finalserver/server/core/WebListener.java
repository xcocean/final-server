package top.lingkang.finalserver.server.core;

import top.lingkang.finalserver.server.web.http.FinalServerContext;

/**
 * @author lingkang
 * 2023/1/5
 * web 生命周期监听
 **/
public interface WebListener {
    /**
     * 接收到了http请求，在处理之前。
     * 此时已经解析了http请求，但还未对其进行处理
     */
    void before(FinalServerContext context) throws Exception;

    /**
     * http处理完毕
     * 此时，已经返回了http响应到客户端。即此时已经断开了连接
     * 常用于事务的最后提交
     */
    void after() throws Exception;
}
