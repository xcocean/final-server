package top.lingkang.finalserver.server.web.http;

import top.lingkang.finalserver.server.core.FinalServerProperties;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;

/**
 * @author lingkang
 * Created by 2022/12/12
 * @since 1.0.0
 */
public class HttpSession implements Session, Serializable {
    private long createTime = System.currentTimeMillis(), lastAccessTime = createTime;
    private String id;
    private final HashMap<String, Object> attributes = new HashMap<>();
    private boolean hasUpdateAttribute;

    public HttpSession() {
    }

    public HttpSession(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public long getCreationTime() {
        return createTime;
    }

    @Override
    public long lastAccessTime() {
        return lastAccessTime;
    }

    @Override
    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    @Override
    public void setAttribute(String name, Object value) {
        hasUpdateAttribute = true;
        attributes.put(name, value);
    }

    @Override
    public boolean hasAttribute(String name) {
        return attributes.containsKey(name);
    }

    @Override
    public void removeAttribute(String name) {
        hasUpdateAttribute = true;
        attributes.remove(name);
    }

    @Override
    public void removeAllAttribute() {
        hasUpdateAttribute = true;
        attributes.clear();
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return new Enumeration<String>() {
            private final Iterator<String> i = new LinkedHashSet(attributes.keySet()).iterator();

            public boolean hasMoreElements() {
                return i.hasNext();
            }

            public String nextElement() {
                return i.next();
            }
        };
    }

    @Override
    public HashMap<String, Object> getAttributeMap() {
        return attributes;
    }

    @Override
    public boolean isExpire() {
        boolean is = System.currentTimeMillis() - lastAccessTime > FinalServerProperties.server_session_age;
        if (is) {
            attributes.clear();
        }
        return is;
    }

    public void updateLastAccessTime() {
        lastAccessTime = System.currentTimeMillis();
    }

    @Override
    public boolean hasUpdateAttribute() {
        return hasUpdateAttribute;
    }
}
