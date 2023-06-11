package top.lingkang.finalserver.server.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.annotation.Order;
import top.lingkang.finalserver.server.web.FinalServerInitializer;
import top.lingkang.finalserver.server.web.FinalServerWeb;
import top.lingkang.finalserver.server.web.handler.ControllerRequestHandler;
import top.lingkang.finalserver.server.web.handler.StaticRequestHandler;
import top.lingkang.finalserver.server.web.ws.WebSocketDispatch;

/**
 * @author lingkang
 * Created by 2023/6/11
 * @since 1.1.0
 */
@Configurable
@EnableConfigurationProperties(FinalServerProperties.class)
@EnableAspectJAutoProxy// AOP
@EnableAutoConfiguration
@ComponentScan("top.lingkang.finalserver.server")
public class InitConfig {
    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    public StaticRequestHandler staticRequestHandler() {
        return new StaticRequestHandler();
    }

    @Bean
    public ControllerRequestHandler controllerRequestHandler() {
        return new ControllerRequestHandler();
    }

    @Bean
    public FinalServerInitializer finalServerInitializer() {
        return new FinalServerInitializer(applicationContext);
    }

    @Bean(initMethod = "startWeb")
    public FinalServerWeb finalServerWeb() {
        return new FinalServerWeb();
    }

    @Order(Integer.MAX_VALUE)// 最后加载
    @Bean
    public WebSocketDispatch websocketManage() {
        return new WebSocketDispatch(applicationContext);
    }


}
