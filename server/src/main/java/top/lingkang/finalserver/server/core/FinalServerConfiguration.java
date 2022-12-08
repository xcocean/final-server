package top.lingkang.finalserver.server.core;

import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import top.lingkang.finalserver.server.core.impl.DefaultSerializableObject;
import top.lingkang.finalserver.server.core.impl.DefaultWebExceptionHandler;
import top.lingkang.finalserver.server.core.impl.FinalServerDefaultHttpHeaders;

/**
 * @author lingkang
 * Created by 2022/12/7
 */
public class FinalServerConfiguration {
    public static HttpHeaders defaultResponseHeaders=new FinalServerDefaultHttpHeaders();

    public static SerializableObject serializable=new DefaultSerializableObject();

    public static WebExceptionHandler webExceptionHandler=new DefaultWebExceptionHandler();
}
