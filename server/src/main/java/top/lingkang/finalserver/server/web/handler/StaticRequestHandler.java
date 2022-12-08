package top.lingkang.finalserver.server.web.handler;

import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.stream.ChunkedFile;
import top.lingkang.finalserver.server.web.http.FinalServerContext;
import top.lingkang.finalserver.server.web.http.HttpResponse;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.URL;

/**
 * @author lingkang
 * Created by 2022/12/8
 */
public class StaticRequestHandler implements RequestHandler {

    @Override
    public boolean handler(FinalServerContext context) throws Exception {
        if (context.getRequest().getPath().contains(".")) {
            URL resource = StaticRequestHandler.class.getClassLoader().getResource("static" + context.getRequest().getPath());
            if (resource != null) {
                context.getResponse().returnFile(resource.getPath());
                return true;
            }
        }
        return false;
    }
}
