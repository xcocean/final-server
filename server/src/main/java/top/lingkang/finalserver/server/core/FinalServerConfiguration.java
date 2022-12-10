package top.lingkang.finalserver.server.core;

import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import top.lingkang.finalserver.server.core.impl.DefaultServerGenerateId;
import top.lingkang.finalserver.server.core.impl.DefaultSerializableObject;
import top.lingkang.finalserver.server.core.impl.DefaultWebExceptionHandler;
import top.lingkang.finalserver.server.core.impl.FinalServerDefaultHttpHeaders;

/**
 * @author lingkang
 * Created by 2022/12/7
 * @since 1.0.0
 */
public class FinalServerConfiguration {
    public static FinalServerDefaultHttpHeaders defaultResponseHeaders=new FinalServerDefaultHttpHeaders();

    public static SerializableObject serializable=new DefaultSerializableObject();

    public static WebExceptionHandler webExceptionHandler=new DefaultWebExceptionHandler();

    public static ServerCookieDecoder cookieDecoder=ServerCookieDecoder.LAX;

    public static ServerGenerateId serverGenerateId =new DefaultServerGenerateId();
}
