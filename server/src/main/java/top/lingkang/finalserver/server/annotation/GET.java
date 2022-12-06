package top.lingkang.finalserver.server.annotation;

import java.lang.annotation.*;

/**
 * @author lingkang
 * Created by 2022/12/7
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GET {
    String name() default "";
}
