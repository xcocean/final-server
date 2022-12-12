package top.lingkang.finalserver.server.web.nio.ws;

import cn.hutool.core.lang.Assert;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import top.lingkang.finalserver.server.annotation.Websocket;
import top.lingkang.finalserver.server.core.FinalServerProperties;
import top.lingkang.finalserver.server.utils.HttpUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author lingkang
 * Created by 2022/12/12
 * websocket 管理，处理调度
 */
public class WebSocketManage {
    private static final Logger log = LoggerFactory.getLogger(WebSocketManage.class);
    private HashMap<String, WebSocketHandler> ws = new HashMap<>();
    private FilterChain filterChain;

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

        // 初始化过滤器
        List<WebSocketFilter> filters = new ArrayList<>();
        names = applicationContext.getBeanNamesForType(WebSocketFilter.class);
        for (String name : names) {
            Object bean = applicationContext.getBean(name);
            filters.add((WebSocketFilter) bean);
        }

        filterChain = new FilterChain(filters.toArray(new WebSocketFilter[]{}), new WsHandler() {
            @Override
            public void handler(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
                int index = msg.uri().indexOf("?");
                String path;
                if (index != -1)
                    path = msg.uri().substring(0, index);
                else
                    path = msg.uri();
                WebSocketHandler handler = getHandler(path);
                if (handler == null) {
                    log.warn("未找到websocket处理, 它将被直接关闭连接. path={}", path);
                    HttpUtils.closeHttpWebsocket(ctx, "404");
                    return;
                }

                // 开始握手连接
                ctx.pipeline().addLast(new WebSocketServerCompressionHandler());
                ctx.pipeline().addLast(new FinalWebSocketServerProtocolHandler(
                        msg.uri(), //路径
                        null,
                        true,
                        FinalServerProperties.websocket_maxMessage, //最大处理数据内容
                        false,  //掩码加密
                        true //允许 websocketPath 路径匹配，否则走全匹配，例如 websocketPath=/ws request=/ws?user=zhangsan 将匹配不上，无法处理
                ));

                //websocket 处理
                ctx.pipeline().addLast(new WebSocketInitializer(handler, msg.headers()));

                // 后续处理
                ctx.fireChannelRead(msg.retain());
            }
        });
    }

    public WebSocketHandler getHandler(String path) {
        return ws.get(path);
    }

    public FilterChain getFilterChain() {
        return filterChain;
    }
}
