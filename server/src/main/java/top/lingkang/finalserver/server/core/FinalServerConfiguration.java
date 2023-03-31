package top.lingkang.finalserver.server.core;

import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import top.lingkang.finalserver.server.core.impl.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lingkang
 * Created by 2022/12/7
 * @since 1.0.0
 */
public class FinalServerConfiguration {
    // 默认响应头
    public static ServerDefaultHttpHeaders defaultResponseHeaders=new FinalServerDefaultHttpHeaders();

    public static SerializableObject serializable=new DefaultSerializableObject();

    // new DefaultWebExceptionHandler(); 异常处理
    public static WebExceptionHandler webExceptionHandler;

    public static ServerCookieDecoder cookieDecoder=ServerCookieDecoder.LAX;

    // 默认 new DefaultIdGenerateFactory(); id生成
    public static IdGenerateFactory idGenerateFactory;

    // DefaultHttpSessionManage 会话管理
    public static HttpSessionManage httpSessionManage;

    // web监听
    public static List<WebListener> webListener=new ArrayList<>();

    // 默认模板解析
    public static HttpParseTemplate httpParseTemplate=new DefaultHttpParseTemplate();

    // 返回文件处理
    public static ReturnStaticFileHandler returnStaticFileHandler=new DefaultReturnStaticFileHandler();
}
