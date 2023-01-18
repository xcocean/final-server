package top.lingkang.finalserver.server.utils;

import com.alibaba.fastjson2.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.lingkang.finalserver.server.core.FinalServerConfiguration;
import top.lingkang.finalserver.server.core.WebListener;
import top.lingkang.finalserver.server.error.FinalServerException;
import top.lingkang.finalserver.server.web.http.FinalServerContext;

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

    public static <T> T paramToBean(String name, Class<T> type, FinalServerContext context) {
        // 此时参数可能是对象
        try {
            Map<String, String> params = context.getRequest().getParams();
            if (params.isEmpty())
                return null;
            return JSON.parseObject(JSON.toJSONString(params), type);
        } catch (Exception e) {
            log.error("尝试给接收参数对象进行赋值异常，接收入参名称：{}  类型： {}", name, type.getName());
            throw new FinalServerException(e);
        }
    }
}
