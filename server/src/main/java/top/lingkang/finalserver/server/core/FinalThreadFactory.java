package top.lingkang.finalserver.server.core;

import java.util.concurrent.ThreadFactory;

/**
 * @author lingkang
 * Created by 2022/12/12
 * @since 1.0.0
 */
public class FinalThreadFactory implements ThreadFactory {
    private static int index = 0;
    private String prefix;

    public FinalThreadFactory(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public Thread newThread(Runnable r) {
        index++;
        return new Thread(r, prefix + "-" + index);
    }
}
