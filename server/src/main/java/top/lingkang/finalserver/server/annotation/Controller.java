package top.lingkang.finalserver.server.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author lingkang
 * Created by 2022/12/6
 * @since 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
@org.springframework.stereotype.Controller
public @interface Controller {
}
