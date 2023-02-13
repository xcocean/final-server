package top.lingkang.finalserver.server.core;

import cn.hutool.core.util.SystemPropsUtil;
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
    public static String debug = "false";
    public static String server_static = "/static";
    public static String log_file = "logback.xml";
    public static int server_thread_maxReceive = 0;
    public static int server_thread_maxHandler = 0;
    public static int server_thread_backlog = 0;
    public static int websocket_maxMessage = 65536;
    public static long websocket_timeout = 12000;
    public static String server_session_name = "sid";
    public static long server_session_age = 1800000;
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

        server_port = SystemPropsUtil.getInt("server.port", server_port);
        debug = SystemPropsUtil.get("debug", debug);
        server_static = SystemPropsUtil.get("server.static", server_static);
        log_file = SystemPropsUtil.get("log.file", log_file);
        server_thread_maxReceive = SystemPropsUtil.getInt("server.thread.maxReceive", server_thread_maxReceive);
        server_thread_maxHandler = SystemPropsUtil.getInt("server.thread.maxHandler", server_thread_maxHandler);
        server_thread_backlog = SystemPropsUtil.getInt("server.thread.backlog", server_thread_backlog);
        websocket_maxMessage = SystemPropsUtil.getInt("websocket.maxMessage", websocket_maxMessage);
        websocket_timeout = SystemPropsUtil.getLong("websocket.timeout", websocket_timeout);
        server_session_name = SystemPropsUtil.get("server.session.name", server_session_name);
        server_session_age = SystemPropsUtil.getLong("server.session.age", server_session_age);
        server_maxContentLength = SystemPropsUtil.getInt("server.maxContentLength", server_maxContentLength);
        server_uploadFileBuffer = SystemPropsUtil.getLong("server.uploadFileBuffer", server_uploadFileBuffer);
        server_fileFtpSize = SystemPropsUtil.getLong("server.fileFtpSize", server_fileFtpSize);

        // 模板引擎配置
        server_template_suffix = SystemPropsUtil.get("server.template.suffix", server_template_suffix);
        server_template_prefix = SystemPropsUtil.get("server.template.prefix", server_template_prefix);
        server_template_cache = SystemPropsUtil.getBoolean("server.template.cache", server_template_cache);
        server_template_cacheTime = SystemPropsUtil.getLong("server.template.cacheTime", server_template_cacheTime);

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
