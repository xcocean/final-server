package top.lingkang.finalserver.server.web;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import top.lingkang.finalserver.server.FinalServerApplication;
import top.lingkang.finalserver.server.core.FinalServerConfiguration;
import top.lingkang.finalserver.server.core.HttpParseTemplate;
import top.lingkang.finalserver.server.core.WebExceptionHandler;
import top.lingkang.finalserver.server.core.impl.DefaultHttpParseTemplate;
import top.lingkang.finalserver.server.core.impl.ShutdownEventWeb;
import top.lingkang.finalserver.server.utils.BeanUtils;
import top.lingkang.finalserver.server.web.handler.*;
import top.lingkang.finalserver.server.web.http.Filter;
import top.lingkang.finalserver.server.web.http.FilterChain;
import top.lingkang.finalserver.server.web.nio.FinalServerNioServerSocketChannel;
import top.lingkang.finalserver.server.web.nio.ServerInitializer;

import javax.annotation.PostConstruct;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author lingkang
 * Created by 2022/12/6
 * @since 1.0.0
 */
public class FinalServerWeb {
    private static final Logger log = LoggerFactory.getLogger(FinalServerWeb.class);
    private EventLoopGroup bossGroup, workGroup;
    @Autowired
    private Environment environment;
    @Autowired
    private ApplicationContext applicationContext;
    private HttpParseTemplate parseTemplate;

    private FilterChain filterChain;


    @PostConstruct
    private void init() {
        List<RequestHandler> handlers = new ArrayList<>();
        String[] beanNamesForType = applicationContext.getBeanNamesForType(LocalStaticMapping.class);
        if (beanNamesForType.length > 0) {
            for (String name : beanNamesForType) {

                LocalStaticMapping bean = (LocalStaticMapping) applicationContext.getBean(name);
                for (String path : bean.getPaths()) {
                    handlers.add(new LocalStaticRequestHandler(path));
                    log.info("本地静态文件映射：" + path);
                }
            }
        }
        handlers.add(new StaticRequestHandler(environment));// 项目静态文件
        handlers.add(new BuildControllerHandler(applicationContext).build());// controller转发
        filterChain = setFilterChain(handlers.toArray(new RequestHandler[0]));

        // 初始化异常处理
        WebExceptionHandler exceptionHandler = BeanUtils.getBean(WebExceptionHandler.class, applicationContext);
        if (exceptionHandler != null)
            FinalServerConfiguration.webExceptionHandler = exceptionHandler;

        // 初始化模板解析
        parseTemplate = BeanUtils.getBean(HttpParseTemplate.class, applicationContext);
        if (parseTemplate == null)// 使用默认模板解析
            parseTemplate = BeanUtils.getBean(DefaultHttpParseTemplate.class, applicationContext);
        parseTemplate.init(environment.getProperty("server.template", "/template"));
    }

    public void run() {
        int port = Integer.valueOf(environment.getProperty("server.port"));
        log.info("FinalServer start web service port: {}", port);

        web(port);
    }

    private void web(int port) {
        int pro = Runtime.getRuntime().availableProcessors();
        int boss = pro * 2, work = pro * 25;
        int receive = Integer.parseInt(environment.getProperty("server.thread.maxReceive", "0"));
        if (receive != 0)
            boss = receive;
        int handler = Integer.parseInt(environment.getProperty("server.thread.maxHandler", "0"));
        if (handler != 0)
            work = handler;
        else if (work > 100)
            work = 100;// 默认值不超过100

        String backlog = environment.getProperty("server.thread.backlog", "256");
        log.info("线程数配置 maxReceive={}  maxHandler={}  backlog={}", boss, work, backlog);

        // 创建 主线程组，主线程接收并把任务丢给从线程，工作线程做处理
        bossGroup = new NioEventLoopGroup(boss);
        // 工作线程组
        workGroup = new NioEventLoopGroup(work);
        FinalServerApplication.addShutdownHook(new ShutdownEventWeb(bossGroup, workGroup));
        // netty服务器创建，ServerBootstrap是一个启动类
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workGroup)      //设置主从线程
                .channel(FinalServerNioServerSocketChannel.class) //  设置nio的双向管道
                //当连接被阻塞时BACKLOG代表的是阻塞队列的长度
                .option(ChannelOption.SO_BACKLOG, Integer.parseInt(backlog))
                //置连接为保持活动的状态
                .childOption(ChannelOption.SO_KEEPALIVE, true);
        // 子处理器
        serverBootstrap.childHandler(
                new ServerInitializer(applicationContext, filterChain, parseTemplate)
        );
        //启动server并绑定端口监听和设置同步方式
        try {
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            log.info(
                    "Started {} in {} seconds (JVM running for {})",
                    FinalServerApplication.mainClass.getSimpleName(),
                    new Double((System.currentTimeMillis() - FinalServerApplication.startTime)) / 1000.0,
                    new Double(ManagementFactory.getRuntimeMXBean().getUptime()) / 1000.0
            );
            log.info("website: http://localhost:" + port);

            new Thread(() -> {
                //关闭监听方式
                try {
                    channelFuture.channel().closeFuture().sync();
                } catch (InterruptedException e) {
                    log.info("FinalServer web 启动异常: ", e);
                    System.exit(0);
                } finally {
                    bossGroup.shutdownGracefully();
                    workGroup.shutdownGracefully();
                }
            }).start();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private FilterChain setFilterChain(RequestHandler[] requestHandlers) {
        // 过滤类
        String[] filters = applicationContext.getBeanNamesForType(Filter.class);
        if (filters.length > 0) {
            List<Filter> list = new ArrayList<>();
            for (String name : filters)
                list.add((Filter) applicationContext.getBean(name));

            // 排序
            list.sort(new Comparator<Filter>() {
                @Override
                public int compare(Filter o1, Filter o2) {
                    Class<? extends Filter> aClass = o1.getClass();
                    int v1 = 0, v2 = 0;
                    Order order = BeanUtils.getSpringProxyToClass(o1.getClass()).getAnnotation(Order.class);
                    if (order != null)
                        v1 = order.value();
                    order = BeanUtils.getSpringProxyToClass(o2.getClass()).getAnnotation(Order.class);
                    if (order != null)
                        v2 = order.value();

                    if (v1 == v2)
                        return 0;

                    return v1 > v2 ? 1 : -1;
                }
            });

            return new FilterChain(list.toArray(new Filter[]{}), requestHandlers);
        }

        return new FilterChain(new Filter[0], requestHandlers);
    }

}
