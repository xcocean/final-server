package top.lingkang.finalserver.server.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author lingkang
 * Created by 2023/6/11
 * @since 1.1.0
 */
@Data
@ConfigurationProperties(prefix = "final.server")
public class FinalServerProperties {
    /**
     * 默认 web 端口
     */
    private int port = 7070;

    /**
     * 静态资源路径 位于 resources/static 下
     */
    private String templateStatic = "/static";

    /**
     * 模板引擎目录 位于 resources/templates 下
     */
    private String templatePath = "/templates";

    /**
     * thymeleaf 模板引擎配置
     */
    private String templateSuffix = ".html";

    /**
     * 是否使用缓存，生产环境建议启用
     */
    private boolean templateCache = false;

    /**
     * 默认1小时，单位毫秒。缓存时间，server.template.cache为true才有效
     */
    private long templateCacheTime = 3600000;


    /**
     * session名称
     */
    private String sessionName = "fid";

    /**
     * session 有效时间为毫秒，默认30分钟
     */
    private long sessionExpire = 1800000;

    // 文件相关
    /**
     * 默认 1MB 大小, netty HttpObjectAggregator聚合内容的最大长度（字节）,netty的限制，超出的大小需要考虑分段上传文件
     * -1或者0 表示 Integer.MAX_VALUE 即2GB内容
     */
    private Integer uploadFileBuffer = 1048576;
    /**
     * 默认3MB，上传文件内存缓冲大小，超出该大小后会将文件存放到临时目录，会话处理完毕后自动删除。
     */
    private Integer maxContentLength = 3145728;

    // 缓存相关
    /**
     * 文件处理缓存，默认值为: true(缓存)对应响应头的 Cache-Control: public。可选：true(缓存)、false(不缓存)
     */
    private boolean cacheControl = true;

    // 线程相关
    /**
     * 接收线程数，不需要太大，压力全在处理线程中，0为默认：系统的核数
     */
    private int threadMaxReceive = 0;
    /**
     * 处理线程数，即处理最大并发数，0为默认：系统的核数 * 50（默认值不会超过128）
     * 一些测试数据参考：threadMaxReceive=6时，处理线程数 --> 128：3500请求/秒；
     */
    private int threadMaxHandler = 0;
    /**
     * 积压队列长度：当达到最大并发数，可以积压的请求数量，默认：256
     */
    private int threadBacklog = 256;


    // websocket相关
    /**
     * 处理消息最大值 字节
     */
    private int websocketMaxMessage = 65536;
    /**
     * 超时
     */
    private int websocketTimeout = 12000;

}
