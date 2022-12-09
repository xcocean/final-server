package top.lingkang.finalserver.server.core.impl;

import top.lingkang.finalserver.server.core.InitAppConfig;
import top.lingkang.finalserver.server.core.ShutdownEvent;

import java.io.File;

/**
 * @author lingkang
 * Created by 2022/12/6
 * @since 1.0.0
 */
public class ShutdownEventRemoveTempConfigFile implements ShutdownEvent {
    @Override
    public void shutdown() throws Exception {
        new File(InitAppConfig.getXmlPage()).delete();
    }
}
