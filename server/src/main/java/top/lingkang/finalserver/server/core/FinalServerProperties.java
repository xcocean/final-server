package top.lingkang.finalserver.server.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author lingkang
 * Created by 2022/12/12
 * @since 1.0.0
 */
public class FinalServerProperties {
    private static final Logger log = LoggerFactory.getLogger(FinalServerProperties.class);
    private static boolean isLoad;
    public static int server_port = 7070;
    public static String debug="false";
    public static String server_static = "/static";
    public static String server_template = "/template";
    public static String log_file = "logback.xml";
    public static int server_thread_maxReceive = 0;
    public static int server_thread_maxHandler = 0;
    public static int server_thread_backlog = 0;
    public static int websocket_maxMessage = 65536;
    public static long websocket_timeout = 12000;
    public static String server_session_name = "sid";
    public static int server_session_age = 1800000;
    public static int server_maxContentLength = 3145728;
    public static long server_uploadFileBuffer = 3145728L;
    public static long server_fileFtpSize = 1048576L;
    public static String server_template_suffix = ".html";
    public static String server_template_prefix = "templates/";
    public static boolean server_template_cache;
    public static long server_template_cacheTime = 3600000;

    public static void load() {
        if (isLoad) {
            log.warn("配置已经加载过了");
            return;
        } else
            isLoad = true;

        server_port = Integer.parseInt(System.getProperty("server.port"));
        debug=System.getProperty("debug");
        server_static = System.getProperty("server.static");
        server_template = System.getProperty("server.template");
        log_file = System.getProperty("log.file");
        server_thread_maxReceive = Integer.parseInt(System.getProperty("server.thread.maxReceive"));
        server_thread_maxHandler = Integer.parseInt(System.getProperty("server.thread.maxHandler"));
        server_thread_backlog = Integer.parseInt(System.getProperty("server.thread.backlog"));
        websocket_maxMessage = Integer.parseInt(System.getProperty("websocket.maxMessage"));
        websocket_timeout = Long.parseLong(System.getProperty("websocket.timeout"));
        server_session_name = System.getProperty("server.session.name");
        server_session_age = Integer.parseInt(System.getProperty("server.session.age"));
        server_maxContentLength = Integer.parseInt(System.getProperty("server.maxContentLength"));
        server_uploadFileBuffer = Long.parseLong(System.getProperty("server.uploadFileBuffer"));
        server_fileFtpSize = Long.parseLong(System.getProperty("server.fileFtpSize"));

        // 模板引擎配置
        server_template_suffix = System.getProperty("server.template.suffix");
        server_template_prefix = System.getProperty("server.template.prefix");
        server_template_cache = Boolean.parseBoolean(System.getProperty("server.template.cache"));
        server_template_cacheTime = Long.parseLong(System.getProperty("server.template.cacheTime"));

        check();
    }

    private static void check() {
        if (server_maxContentLength == -1 || server_maxContentLength == 0)
            server_maxContentLength = Integer.MAX_VALUE;

        if (server_template_prefix.startsWith("/"))
            server_template_prefix = server_template_prefix.substring(1);
        if (!server_template_prefix.endsWith("/")) {
            server_template_prefix = server_template_prefix + "/";
        }
    }
}
