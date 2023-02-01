package top.lingkang.finalserver.server.annotation;

import java.lang.annotation.*;

/**
 * @author lingkang
 * 2023/2/1
 * 获取请求参数，从请求属性、请求体中获取，赋值到方法参数中，若spring的版本不支持获取参数，则应该添加该注解
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface RequestParam {
    String value() default "";
}
