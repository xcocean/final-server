package top.lingkang.finalserver.server.web.security.base;

/**
 * @author lingkang
 * Created by 2022/10/28
 * @since 3.0.0
 */
public interface FinalSessionObject<T> {
    Object getAttribute(String name);

    void init(T session);
}
