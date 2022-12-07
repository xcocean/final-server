package top.lingkang.finalserver.server.annotation;

import java.lang.annotation.*;

/**
 * @author lingkang
 * Created by 2022/12/7
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE})
public @interface NotNull {
}
