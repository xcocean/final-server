package top.lingkang.finalserver.server.web.ws;

import cn.hutool.core.lang.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import top.lingkang.finalserver.server.annotation.Websocket;

import java.util.HashMap;

/**
 * @author lingkang
 * Created by 2022/12/12
 * @since 1.0.0
 * websocket 处理调度
 */
public class WebSocketDispatch {
    private static final Logger log = LoggerFactory.getLogger(WebSocketDispatch.class);
    private HashMap<String, WebSocketHandler> ws = new HashMap<>();

    public WebSocketDispatch(ApplicationContext applicationContext) {
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

            Assert.isFalse(
                    ws.containsKey(value),
                    "存在重复的websocket处理，" + value + " 请检查：" + bean.getClass().getName()
                            + "      " + ws.get(value).getClass().getName()
            );
            ws.put(value, (WebSocketHandler) bean);
            log.debug("add websocket 处理：{} - {}", value, bean.getClass().getName());
        }

        // 初始化 websocket的监听
        names = applicationContext.getBeanNamesForType(WebSocketListener.class);
        for (String name : names) {
            WebSocketDispatchManage.listener.add(applicationContext.getBean(name, WebSocketListener.class));
        }
        // 对监听列表进行排序
        if (!WebSocketDispatchManage.listener.isEmpty()) {
            WebSocketDispatchManage.listener.sort((o1, o2) -> {
                Order order1 = o1.getClass().getAnnotation(Order.class);
                Order order2 = o2.getClass().getAnnotation(Order.class);
                if (order1.value() == order2.value())
                    return 0;
                return order1.value() > order2.value() ? 1 : -1;
            });
        }
    }

    public WebSocketHandler getHandler(String path) {
        return ws.get(path);
    }
}
