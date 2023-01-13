package top.lingkang.finalserver.server.annotation;

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
public @interface FinalServerBoot {
    /**
     * 默认加载 resources/application.properties
     */
    String value() default "application.properties";
}
