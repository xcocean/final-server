package top.lingkang.finalserver.server.core;

/**
 * @author lingkang
 * Created by 2022/12/8
 * @since 1.0.0
 */
public interface SerializableObject {

    byte[] stringTo(String str);

    byte[] jsonTo(Object json);
}
