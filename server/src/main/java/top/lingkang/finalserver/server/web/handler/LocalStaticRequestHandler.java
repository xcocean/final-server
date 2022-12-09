package top.lingkang.finalserver.server.web.handler;

import top.lingkang.finalserver.server.web.http.FinalServerContext;

import java.io.File;

/**
 * @author lingkang
 * Created by 2022/12/8
 * @since 1.0.0
 */
public class LocalStaticRequestHandler implements RequestHandler {
    private String basePath = "";

    public LocalStaticRequestHandler(String path) {
        this.basePath = path;
        if (basePath.endsWith("/"))
            basePath = basePath.substring(0, basePath.length() - 1);
        if (basePath.endsWith("//"))
            basePath = basePath.substring(0, basePath.length() - 2);
        if (basePath.endsWith("\\\\"))
            basePath = basePath.substring(0, basePath.length() - 2);
        if (basePath.endsWith("\\"))
            basePath = basePath.substring(0, basePath.length() - 1);
    }

    @Override
    public boolean handler(FinalServerContext context) throws Exception {
        String path = context.getRequest().getPath();
        if (path.contains(".")) {
            File file = new File(basePath + path);
            if (file.exists()) {
                context.getResponse().returnFile(file.getAbsolutePath());
                return true;
            }
        }
        return false;
    }
}
