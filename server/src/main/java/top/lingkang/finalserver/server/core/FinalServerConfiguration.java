package top.lingkang.finalserver.server.core;

import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import top.lingkang.finalserver.server.core.impl.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lingkang
 * Created by 2022/12/7
 * @since 1.0.0
 */
public class FinalServerConfiguration {
    // 默认响应头
    public static ServerDefaultHttpHeaders defaultResponseHeaders=new FinalServerDefaultHttpHeaders();

    public static SerializableObject serializable=new DefaultSerializableObject();

    // new DefaultWebExceptionHandler(); 异常处理
    public static WebExceptionHandler webExceptionHandler=new DefaultWebExceptionHandler();

    public static ServerCookieDecoder cookieDecoder=ServerCookieDecoder.LAX;

    // 默认 new DefaultIdGenerateFactory(); id生成
    public static IdGenerateFactory idGenerateFactory=new DefaultIdGenerateFactory();

    // DefaultHttpSessionManage 会话管理
    public static HttpSessionManage httpSessionManage;

    // web监听
    public static List<WebListener> webListener=new ArrayList<>();

    // 默认模板解析
    public static HttpParseTemplate httpParseTemplate=new DefaultHttpParseTemplate();

    // 返回文件处理
    public static ReturnStaticFileHandler returnStaticFileHandler=new DefaultReturnStaticFileHandler();

    /**
     * 下面是配置项
     * -------------------------------------------------------------------------------------------------------
     */



    /**
     * 默认 web 端口
     */
    public static int port = 7070;

    /**
     * 静态资源路径 位于 resources/static 下
     */
    public static String templateStatic = "/static";

    /**
     * 模板引擎目录 位于 resources/templates 下
     */
    public static String templatePath = "/templates";

    /**
     * thymeleaf 模板引擎配置
     */
    public static String templateSuffix = ".html";

    /**
     * 是否使用缓存，生产环境建议启用
     */
    public static boolean templateCache = false;

    /**
     * 默认1小时，单位毫秒。缓存时间，server.template.cache为true才有效
     */
    public static long templateCacheTime = 3600000;


    /**
     * session名称
     */
    public static String sessionName = "fid";

    /**
     * session 有效时间为毫秒，默认30分钟
     */
    public static long sessionExpire = 1800000;

    // 文件相关
    /**
     * 默认 3MB 大小, netty HttpObjectAggregator聚合内容的最大长度（字节）,
     * -1或者0 表示 Integer.MAX_VALUE 即2GB内容，netty的限制，超出的大小需要考虑分段上传文件
     */
    public static long uploadFileBuffer = 3145728;
    /**
     * 默认3MB，上传文件内存缓冲大小，超出该大小后会将文件存放到临时目录，会话处理完毕后自动删除。
     */
    public static Integer maxContentLength = 3145728;

    // 缓存相关
    /**
     * 文件处理缓存，默认值为: true(缓存)对应响应头的 Cache-Control: public。可选：true(缓存)、false(不缓存)
     */
    public static boolean cacheControl = true;

    // 线程相关
    /**
     * 接收线程数，不需要太大，压力全在处理线程中，0为默认：系统的核数
     */
    public static int threadMaxReceive = 0;
    /**
     * 处理线程数，即处理最大并发数，0为默认：系统的核数 * 50（默认值不会超过200），对比，springboot默认为200
     */
    public static int threadMaxHandler = 0;
    /**
     * 积压队列长度：当达到最大并发数，可以积压的请求数量，默认：256
     */
    public static int threadBacklog = 256;


    // websocket相关
    /**
     * 处理消息最大值 字节
     */
    public static int websocketMaxMessage = 65536;
    /**
     * 超时
     */
    public static int websocketTimeout = 12000;
}
