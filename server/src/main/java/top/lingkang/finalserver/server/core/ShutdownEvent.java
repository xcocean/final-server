package top.lingkang.finalserver.server.core;

/**
 * @author lingkang
 * Created by 2022/12/6
 */
public interface ShutdownEvent {
    void shutdown() throws Exception;
}