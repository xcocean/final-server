package top.lingkang.finalserver.server.utils;

import java.io.*;

/**
 * @author lingkang
 * Created by 2023/2/5
 * 对象序列化与反序列化
 */
public class SerializableUtils {

    /**
     * 反序列化
     */
    public static <T> T byteToObject(byte[] obj) {
        try {
            ObjectInputStream stream = new ObjectInputStream(new ByteArrayInputStream(obj));
            T session = (T) stream.readObject();
            stream.close();
            return session;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 序列化
     */
    public static byte[] objectToByte(Object obj) {
        ByteArrayOutputStream byt = new ByteArrayOutputStream();
        try {
            ObjectOutputStream stream = new ObjectOutputStream(byt);
            stream.writeObject(obj);
            stream.close();
            return byt.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                byt.close();
            } catch (IOException ignored) {
            }
        }
        return null;
    }
}
