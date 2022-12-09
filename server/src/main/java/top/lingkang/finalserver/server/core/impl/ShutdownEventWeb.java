package top.lingkang.finalserver.server.core.impl;

import io.netty.channel.EventLoopGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.lingkang.finalserver.server.core.ShutdownEvent;

/**
 * @author lingkang
 * Created by 2022/12/6
 * @since 1.0.0
 */
public class ShutdownEventWeb implements ShutdownEvent {
    private static final Logger log= LoggerFactory.getLogger(ShutdownEventWeb.class);
    private EventLoopGroup[] group;

    public ShutdownEventWeb(EventLoopGroup... group) {
        this.group = group;
    }

    @Override
    public void shutdown() throws Exception {
        for (EventLoopGroup g : group)
            if (g != null && !g.isShutdown()) {
                g.shutdownGracefully();
            }

        log.info("close web...");
    }
}
