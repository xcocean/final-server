package top.lingkang.finalserver.server.web;

import cn.hutool.core.net.NetUtil;
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
import top.lingkang.finalserver.server.core.impl.ShutdownEventWeb;
import top.lingkang.finalserver.server.utils.ProxyBeanUtils;
import top.lingkang.finalserver.server.web.handler.BuildControllerHandler;
import top.lingkang.finalserver.server.web.handler.ControllerRequestHandler;
import top.lingkang.finalserver.server.web.handler.RequestHandler;
import top.lingkang.finalserver.server.web.handler.StaticRequestHandler;
import top.lingkang.finalserver.server.web.http.Filter;
import top.lingkang.finalserver.server.web.http.FilterChain;
import top.lingkang.finalserver.server.web.nio.FinalServerNioServerSocketChannel;
import top.lingkang.finalserver.server.web.nio.ServerInitializer;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author lingkang
 * Created by 2022/12/6
 */
public class FinalServerWeb {
    private static final Logger log = LoggerFactory.getLogger(FinalServerWeb.class);
    private EventLoopGroup mainGroup, subGroup;
    @Autowired
    private Environment environment;
    @Autowired
    private ApplicationContext applicationContext;

    private FilterChain filterChain;


    @PostConstruct
    private void init() {
        ControllerRequestHandler controller = new BuildControllerHandler(applicationContext).build();

        filterChain = setFilterChain(new RequestHandler[]{
                new StaticRequestHandler(),
                controller
        });
    }

    public void run() {
        int port = Integer.valueOf(environment.getProperty("server.port"));
        log.info("FinalServer start web service port: {}", port);
        if (!NetUtil.isUsableLocalPort(port)) {
            log.error("FinalServer start fail 端口被占用: {}", port);
            System.exit(0);
        }

        web(port);
    }

    private void web(int port) {
        //创建 主线程组，主线程接收并把任务丢给从线程，从线程做处理
        mainGroup = new NioEventLoopGroup();
        //从线程组
        subGroup = new NioEventLoopGroup();
        FinalServerApplication.addShutdownHook(new ShutdownEventWeb(mainGroup, subGroup));
        // netty服务器创建，ServerBootstrap是一个启动类
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(mainGroup, subGroup)      //设置主从线程
                .channel(FinalServerNioServerSocketChannel.class) //  设置nio的双向管道
                //当连接被阻塞时BACKLOG代表的是阻塞队列的长度
                .option(ChannelOption.SO_BACKLOG, 256)
                //置连接为保持活动的状态
                .childOption(ChannelOption.SO_KEEPALIVE, true);
        // 子处理器
        serverBootstrap.childHandler(new ServerInitializer(applicationContext, filterChain));
        //启动server并绑定端口监听和设置同步方式
        try {
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            log.info("web start success , website: http://localhost:" + port);

            new Thread(() -> {
                //关闭监听方式
                try {
                    channelFuture.channel().closeFuture().sync();
                } catch (InterruptedException e) {
                    log.info("FinalServer web 启动异常: ", e);
                    System.exit(0);
                } finally {
                    mainGroup.shutdownGracefully();
                    subGroup.shutdownGracefully();
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
                    Order order = ProxyBeanUtils.getSpringProxyToClass(o1.getClass()).getAnnotation(Order.class);
                    if (order != null)
                        v1 = order.value();
                    order = ProxyBeanUtils.getSpringProxyToClass(o2.getClass()).getAnnotation(Order.class);
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
