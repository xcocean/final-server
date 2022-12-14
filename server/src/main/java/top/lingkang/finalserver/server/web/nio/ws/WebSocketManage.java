package top.lingkang.finalserver.server.web.nio.ws;

import cn.hutool.core.lang.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import top.lingkang.finalserver.server.annotation.Websocket;

import java.util.HashMap;

/**
 * @author lingkang
 * Created by 2022/12/12
 * websocket 管理，处理调度
 */
public class WebSocketManage {
    private static final Logger log = LoggerFactory.getLogger(WebSocketManage.class);
    private HashMap<String, WebSocketHandler> ws = new HashMap<>();

    public WebSocketManage(ApplicationContext applicationContext) {
        // init
        String[] names = applicationContext.getBeanNamesForType(WebSocketHandler.class);
        if (names.length == 0)
            return;
        for (String name : names) {
            Object bean = applicationContext.getBean(name);
            Websocket annotation = bean.getClass().getAnnotation(Websocket.class);
            if (annotation == null) {
                log.warn("bean：{}  {} 未添加 @Websocket 注解，将无法注册websocket处理", name, bean.getClass().getName());
                continue;
            }

            String value = annotation.value();
            if (!value.startsWith("/"))
                value = "/" + value;

            Assert.isFalse(ws.containsKey(value), "存在重复的websocket处理，" + value + " 请检查");
            ws.put(value, (WebSocketHandler) bean);
            log.debug("add websocket 处理：{} - {}", value, bean.getClass().getName());
        }
    }

    public WebSocketHandler getHandler(String path) {
        return ws.get(path);
    }
}
