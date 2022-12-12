package top.lingkang.finalserver.server.web.http;

import top.lingkang.finalserver.server.core.FinalServerProperties;

import java.util.*;

/**
 * @author lingkang
 * Created by 2022/12/12
 */
public class HttpSession implements Session {
    private long createTime = System.currentTimeMillis(), lastAccessTime = createTime;
    private String id;
    private final HashMap<String, Object> attributes = new HashMap<>();

    public HttpSession(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public long getCreationTime() {
        return 0;
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
        attributes.put(name, value);
    }

    @Override
    public void removeAttribute(String name) {
        attributes.remove(name);
    }

    @Override
    public void removeAllAttribute(String name) {
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
    public boolean hasAttribute() {
        return !attributes.isEmpty();
    }

    @Override
    public boolean isExpire() {
        return System.currentTimeMillis() - lastAccessTime > FinalServerProperties.server_session_age * 1000;
    }

    public void access() {
        lastAccessTime = System.currentTimeMillis();
    }
}
