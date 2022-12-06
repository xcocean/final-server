package top.lingkang.finalserver.server.web.http;

import java.io.InputStream;

/**
 * @author lingkang
 * Created by 2022/12/6
 */
public interface Response {
    void setHeader(String name,String value);

    InputStream getInputStream();

    void setStatusCode(int code);


}
