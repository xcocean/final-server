package top.lingkang.finalserver.server.web.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.multipart.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.lingkang.finalserver.server.core.FinalServerConfiguration;
import top.lingkang.finalserver.server.core.FinalServerProperties;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author lingkang
 * Created by 2022/12/6
 * @since 1.0.0
 */
public class HttpRequest implements Request {
    private ChannelHandlerContext ctx;
    private FullHttpRequest msg;
    private QueryStringDecoder queryUri;
    private HttpPostRequestDecoder queryBody;
    private Set<Cookie> cookies;
    private Session session;
    List<MultipartFile> fileList = new ArrayList<>();
    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);

    public HttpRequest(ChannelHandlerContext ctx, FullHttpRequest msg) {
        this.ctx = ctx;
        this.msg = msg;
    }

    @Override
    public String requestId() {
        return ctx.channel().id().asLongText();
    }

    @Override
    public String getPath() {
        checkQueryUri();
        return queryUri.path();
    }

    @Override
    public String getParam(String name) {
        if (msg.method() == HttpMethod.POST) {
            checkQueryBody();
            InterfaceHttpData data = queryBody.getBodyHttpData(name);
            if (data != null) {
                if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
                    try {
                        return ((Attribute) data).getValue();
                    } catch (IOException e) {
                    }
                }
            }
        }

        // get 请求
        checkQueryUri();
        List<String> put = queryUri.parameters().get(name);
        return put.get(0);
    }

    @Override
    public List<MultipartFile> getFileUpload() {
        if (!fileList.isEmpty())
            return fileList;
        checkQueryBody();
        List<InterfaceHttpData> httpDatas = queryBody.getBodyHttpDatas();
        for (InterfaceHttpData data : httpDatas) {
            if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.FileUpload) {
                FileUpload fileUpload = (FileUpload) data;
                fileList.add(new MultipartFile(fileUpload));
            }
        }
        return fileList;
    }

    @Override
    public String getHeader(String name) {
        return msg.headers().get(name);
    }

    @Override
    public HttpHeaders getHeaders() {
        return msg.headers();
    }

    @Override
    public String getIp() {
        return ((InetSocketAddress) ctx.channel().remoteAddress()).getHostString();
    }

    @Override
    public int getPort() {
        return ((InetSocketAddress) ctx.channel().remoteAddress()).getPort();
    }

    @Override
    public HttpMethod getHttpMethod() {
        return msg.method();
    }

    @Override
    public Cookie getCookie(String name) {
        for (Cookie cookie : getCookies()) {
            if (cookie.name().equals(name))
                return cookie;
        }
        return null;
    }

    @Override
    public Set<Cookie> getCookies() {
        if (cookies != null)
            return cookies;
        String cookie = msg.headers().get(HttpHeaderNames.COOKIE);
        if (cookie != null) {
            try {
                cookies = FinalServerConfiguration.cookieDecoder.decode(cookie);
                return cookies;
            } catch (Exception e) {
                log.warn("", e);
            }
        }
        return new TreeSet<>();
    }

    @Override
    public Session getSession() {
        if (session == null || System.currentTimeMillis() - session.lastAccessTime() > FinalServerProperties.server_session_age)
            session = FinalServerConfiguration.httpSessionManage.getSession(FinalServerContext.currentContext().getRequest());
        return session;
    }

    @Override
    public void release() {
        // 上传的是文件才执行释放
//        if (queryBody != null && queryBody.isMultipart())
//            queryBody.destroy(); // https://github.com/netty/netty/issues/10351
        if (!fileList.isEmpty()) {
            for (MultipartFile multipartFile : fileList) {
                try {
                    if (multipartFile.isInMemory()) {
                        if (multipartFile.getFile() != null)
                            multipartFile.getFile().delete();
                    } else {
                        multipartFile.getFileUpload().getFile().delete();
                    }
                } catch (Exception e) {
                    log.warn("", e);
                }
            }
        }
    }

    // 首次获取时再实例化，提升性能
    private void checkQueryUri() {
        if (queryUri == null)
            queryUri = new QueryStringDecoder(msg.uri());
    }

    // 首次获取时再实例化，提升性能
    private void checkQueryBody() {
        if (queryBody == null) {
            queryBody = new HttpPostRequestDecoder(new DefaultHttpDataFactory(FinalServerProperties.server_uploadFileBuffer), msg);
        }
    }
}
