package top.lingkang.finalserver.server.web.http;

import com.alibaba.fastjson2.JSON;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.multipart.*;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.lingkang.finalserver.server.core.FinalServerConfiguration;
import top.lingkang.finalserver.server.core.FinalServerProperties;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;

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
        if (msg.method() != HttpMethod.GET) {
            checkQueryBody();
            InterfaceHttpData data = queryBody.getBodyHttpData(name);
            if (data != null && data.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
                try {
                    return ((Attribute) data).getValue();
                } catch (IOException e) {
                    log.warn("获取参数内容失败：" + name, e);
                }
            }
        }

        // get 请求
        checkQueryUri();
        List<String> param = queryUri.parameters().get(name);
        if (param == null)
            return null;
        return param.get(0);
    }

    @Override
    public String getBody() {
        if (msg.method() != HttpMethod.GET) {
            return msg.content().toString(CharsetUtil.UTF_8);
        }
        return null;
    }

    @Override
    public <T> T getParamToBean(Class<T> beanClass) {
        Map<String, String> params = getParams();
        if (params.isEmpty())
            return null;
        return JSON.parseObject(JSON.toJSONString(params), beanClass);
    }

    @Override
    public Map<String, String> getParams() {
        Map<String, String> map = new HashMap<>();
        checkQueryUri();
        Map<String, List<String>> parameters = queryUri.parameters();
        for (Map.Entry<String, List<String>> entry : parameters.entrySet()) {
            if (!entry.getValue().isEmpty())
                map.put(entry.getKey(), entry.getValue().get(0));
        }

        // 其他请求的入参，将url的入参覆盖
        if (msg.method() == HttpMethod.POST || msg.method() == HttpMethod.PUT || msg.method() == HttpMethod.DELETE) {
            checkQueryBody();
            List<InterfaceHttpData> bodyHttpDatas = queryBody.getBodyHttpDatas();
            for (InterfaceHttpData data : bodyHttpDatas) {
                if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
                    try {
                        map.put(data.getName(), ((Attribute) data).getValue());
                    } catch (IOException e) {
                        log.warn("获取参数内容失败：" + data.getName(), e);
                    }
                }
            }
        }
        return map;
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

    @Override
    public FullHttpRequest getFullHttpRequest() {
        return msg;
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
