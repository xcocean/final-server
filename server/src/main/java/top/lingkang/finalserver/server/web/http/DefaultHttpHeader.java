package top.lingkang.finalserver.server.web.http;

import io.netty.handler.codec.http.DefaultHttpHeaders;
import top.lingkang.finalserver.server.constant.FinalServerConstants;

/**
 * @author lingkang
 * Created by 2022/12/7
 * 默认的响应头
 */
public class DefaultHttpHeader extends DefaultHttpHeaders {
    public DefaultHttpHeader() {
        add("Server", "Final-Server " + FinalServerConstants.version);
    }
}
