package top.lingkang.finalserver.server.annotation;

import java.lang.annotation.*;

/**
 * @author lingkang
 * Created by 2022/12/6
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@org.springframework.stereotype.Controller
public @interface Controller {
}
