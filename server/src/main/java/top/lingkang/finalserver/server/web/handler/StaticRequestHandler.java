package top.lingkang.finalserver.server.web.handler;

import top.lingkang.finalserver.server.core.FinalServerProperties;
import top.lingkang.finalserver.server.web.entity.ResponseFile;
import top.lingkang.finalserver.server.web.http.FinalServerContext;

import java.net.URL;
import java.net.URLDecoder;

/**
 * @author lingkang
 * Created by 2022/12/8
 * @since 1.0.0
 */
public class StaticRequestHandler implements RequestHandler {

    private String basePath = "";

    public StaticRequestHandler() {
        basePath = FinalServerProperties.server_static;
        if (basePath.startsWith("/"))
            basePath = basePath.substring(1);
    }

    @Override
    public boolean handler(FinalServerContext context) throws Exception {
        if (context.getRequest().getPath().contains(".")) {
            URL resource = StaticRequestHandler.class.getClassLoader().getResource(basePath + context.getRequest().getPath());
            if (resource != null) {
                context.getResponse().returnFile(new ResponseFile(URLDecoder.decode(resource.getPath(), "UTF-8")));
                return true;
            }
        }
        return false;
    }
}
