package top.lingkang.finalserver.example.app;

import org.springframework.context.annotation.Configuration;
import top.lingkang.finalserver.server.web.security.annotation.EnableFinalSecurityAnnotation;
import top.lingkang.finalserver.server.web.security.base.FinalHttpProperties;
import top.lingkang.finalserver.server.web.security.config.FinalSecurityConfiguration;

/**
 * @author lingkang
 * 2023/1/11
 **/
@EnableFinalSecurityAnnotation // 开启 FinalSecurity AOP鉴权注解
@Configuration
public class Myconfig extends FinalSecurityConfiguration {
    @Override
    protected void config(FinalHttpProperties properties) {
        // 对项目进行配置
        properties.checkAuthorize()
                .pathMatchers("/t").hasAnyRole("user", "vip1") // 有其中任意角色就能访问
                .pathMatchers("/vip/**").hasAllRole("user", "vip1") // 必须有所有角色才能访问
                .pathMatchers("/about").hasLogin();// 需要登录才能访问
    }
}