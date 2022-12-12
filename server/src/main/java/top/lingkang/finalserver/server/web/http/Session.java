package top.lingkang.finalserver.server.web.http;

import java.util.Enumeration;

/**
 * @author lingkang
 * Created by 2022/12/12
 */
public interface Session {
    String getId();

    long getCreationTime();

    long lastAccessTime();

    Object getAttribute(String name);

    void setAttribute(String name, Object value);

    void removeAttribute(String name);

    void removeAllAttribute(String name);

    Enumeration<String> getAttributeNames();

    boolean hasAttribute();

    boolean isExpire();
}
