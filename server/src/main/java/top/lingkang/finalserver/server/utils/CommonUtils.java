package top.lingkang.finalserver.server.utils;

import com.alibaba.fastjson2.JSON;
import io.netty.handler.codec.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.lingkang.finalserver.server.annotation.RequestHeader;
import top.lingkang.finalserver.server.core.FinalServerConfiguration;
import top.lingkang.finalserver.server.core.WebListener;
import top.lingkang.finalserver.server.error.FinalServerException;
import top.lingkang.finalserver.server.web.handler.MethodHandlerParam;
import top.lingkang.finalserver.server.web.http.FinalServerContext;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lingkang
 * Created by 2022/12/8
 * @since 1.0.0
 */
public class CommonUtils {
    private static final Logger log = LoggerFactory.getLogger(CommonUtils.class);

    public static void pushWebListenerBefore(FinalServerContext context) throws Exception {
        for (WebListener listener : FinalServerConfiguration.webListener) {
            listener.before(context);
        }
    }

    public static void pushWebListenerAfter() throws Exception {
        for (WebListener listener : FinalServerConfiguration.webListener) {
            listener.after();
        }
    }

    public static <T> T paramToBean(String name, Class<T> type, Annotation annotation, FinalServerContext context) {
        // 此时参数可能是对象
        try {
            if (annotation instanceof RequestHeader) {
                HttpHeaders headers = context.getRequest().getHeaders();
                Map<String, String> map = new HashMap<>();
                for (String key : headers.names()) {
                    map.put(key, headers.get(key));
                }
                return JSON.parseObject(JSON.toJSONString(map), type);
            }
            Map<String, String> params = context.getRequest().getParams();
            if (params.isEmpty())
                return null;
            return JSON.parseObject(JSON.toJSONString(params), type);
        } catch (Exception e) {
            log.error("尝试给接收参数对象进行赋值异常，接收入参名称：{}  类型： {}", name, type.getName());
            throw new FinalServerException(e);
        }
    }

    public static final MethodHandlerParam handlerParam = new MethodHandlerParam();

    public static String getDir() {
        String dir = System.getProperty("user.dir");
        if (dir.endsWith("/"))
            dir = dir.substring(0, dir.length() - 1);
        return dir;
    }
}
