package top.lingkang.finalserver.server.web.http;

import java.util.Enumeration;
import java.util.HashMap;

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

    HashMap<String,Object> getAttributeMap();

    boolean hasAttribute();

    boolean isExpire();
}
