package top.lingkang.finalserver.server.core;

import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import top.lingkang.finalserver.server.core.impl.*;

/**
 * @author lingkang
 * Created by 2022/12/7
 * @since 1.0.0
 */
public class FinalServerConfiguration {
    public static FinalServerDefaultHttpHeaders defaultResponseHeaders=new FinalServerDefaultHttpHeaders();

    public static SerializableObject serializable=new DefaultSerializableObject();

    // new DefaultWebExceptionHandler();
    public static WebExceptionHandler webExceptionHandler;

    public static ServerCookieDecoder cookieDecoder=ServerCookieDecoder.LAX;

    // 默认 new DefaultIdGenerateFactory();
    public static IdGenerateFactory idGenerateFactory;

    // DefaultHttpSessionManage
    public static HttpSessionManage httpSessionManage;
}
