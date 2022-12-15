package top.lingkang.finalserver.server.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author lingkang
 * Created by 2022/12/12
 */
public class FinalServerProperties {
    private static final Logger log = LoggerFactory.getLogger(FinalServerProperties.class);
    private static boolean isLoad;
    public static int server_port = 7070;
    public static String server_static = "/static";
    public static String server_template = "/template";
    public static String log_file = "logback.xml";
    public static int server_thread_maxReceive = 0;
    public static int server_thread_maxHandler = 0;
    public static int server_thread_backlog = 0;
    public static int websocket_maxMessage = 65536;
    public static long websocket_timeout = 12000;
    public static String server_session_name = "sid";
    public static int server_session_age = 1800;
    public static int server_maxContentLength = 1024;

    public static void load() {
        if (isLoad) {
            log.warn("配置已经加载过了");
            return;
        } else
            isLoad = true;

        server_port = Integer.parseInt(System.getProperty("server.port"));
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
    }
}
