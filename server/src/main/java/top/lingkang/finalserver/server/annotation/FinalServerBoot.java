package top.lingkang.finalserver.server.annotation;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import top.lingkang.finalserver.server.config.InitConfig;

import java.lang.annotation.*;

/**
 * @author lingkang
 * Created by 2022/12/6
 * @since 1.0.0
 * 可以通过value指定启动时的配置文件
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@SpringBootApplication
@Import(InitConfig.class)
public @interface FinalServerBoot {
}
