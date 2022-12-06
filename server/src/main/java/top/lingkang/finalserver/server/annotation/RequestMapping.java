package top.lingkang.finalserver.server.annotation;

import top.lingkang.finalserver.server.web.http.RequestMethod;

import java.lang.annotation.*;

/**
 * @author lingkang
 * Created by 2022/12/7
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestMapping {
    String name() default "";

    RequestMethod method() default RequestMethod.GET;
}
