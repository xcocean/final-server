package top.lingkang.finalserver.server.web.security.annotation;

import java.lang.annotation.*;

/**
 * @author lingkang
 * Created by 2022/1/11
 * @since 1.0.0
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FinalCheckLogin {
}
