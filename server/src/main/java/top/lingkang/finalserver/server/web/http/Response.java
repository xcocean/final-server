package top.lingkang.finalserver.server.web.http;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author lingkang
 * Created by 2022/12/6
 */
public interface Response {
    void setHeader(String name,String value);

    void writeString(Object obj);

    void setStatusCode(int code);

    boolean isWrite();

}
