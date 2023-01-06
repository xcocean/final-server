package top.lingkang.finalserver.server.web;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.lingkang.finalserver.server.FinalServerApplication;
import top.lingkang.finalserver.server.core.FinalServerProperties;
import top.lingkang.finalserver.server.core.FinalThreadFactory;
import top.lingkang.finalserver.server.core.impl.ShutdownEventWeb;
import top.lingkang.finalserver.server.web.nio.FinalServerNioServerSocketChannel;
import top.lingkang.finalserver.server.web.nio.HandlerNioInitializer;

import java.lang.management.ManagementFactory;

/**
 * @author lingkang
 * Created by 2022/12/6
 * @since 1.0.0
 */
public class FinalServerWeb {
    private static final Logger log = LoggerFactory.getLogger(FinalServerWeb.class);
    private EventLoopGroup bossGroup;
    public static EventLoopGroup workGroup;

    private void init() {
        // 运行
        run();
    }

    public void run() {
        int port = FinalServerProperties.server_port;
        log.info("FinalServer start web service port: {}", port);

        web(port);
    }

    private void web(int port) {
        int pro = Runtime.getRuntime().availableProcessors();
        int boss = pro, work = pro * 50;
        if (FinalServerProperties.server_thread_maxReceive != 0)
            boss = FinalServerProperties.server_thread_maxReceive;
        if (FinalServerProperties.server_thread_maxHandler != 0)
            work = FinalServerProperties.server_thread_maxHandler;
        else if (work > 200)
            work = 200;// 默认值不超过200

        log.info("线程数配置 maxReceive={}  maxHandler={}  backlog={}", boss, work, FinalServerProperties.server_thread_backlog);

        // 创建 主线程组，主线程接收并把任务丢给从线程，工作线程做处理
        bossGroup = new NioEventLoopGroup(boss, new FinalThreadFactory("receive"));
        // 工作线程组
        workGroup = new NioEventLoopGroup(work, new FinalThreadFactory("handler"));
        FinalServerApplication.addShutdownHook(new ShutdownEventWeb(bossGroup, workGroup));
        // netty服务器创建，ServerBootstrap是一个启动类
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workGroup)      //设置主从线程
                .channel(FinalServerNioServerSocketChannel.class) //  设置nio的双向管道
                //当连接被阻塞时BACKLOG代表的是阻塞队列的长度
                .option(ChannelOption.SO_BACKLOG, FinalServerProperties.server_thread_backlog)
                // 禁用Nagle算法，即使用小数据包即时传输
                .childOption(ChannelOption.TCP_NODELAY, true)
                // 自动读取 关闭
                .option(ChannelOption.AUTO_CLOSE, false)
                // 客户端关闭连接时，自动关闭channel
                .childOption(ChannelOption.ALLOW_HALF_CLOSURE, true)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                //置连接为保持活动的状态
                .childOption(ChannelOption.SO_KEEPALIVE, true);
        // 子处理器
        serverBootstrap.childHandler(
                new HandlerNioInitializer()
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

}
