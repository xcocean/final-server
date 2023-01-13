package top.lingkang.finalserver.server.web.security.annotation;

import org.springframework.context.annotation.Import;
import top.lingkang.finalserver.server.web.security.annotation.impl.FinalCheckAnnotation;
import top.lingkang.finalserver.server.web.security.annotation.impl.FinalCheckLoginAnnotation;

import java.lang.annotation.*;

/**
 * @author lingkang
 * Created by 2022/1/11
 * @since 1.0.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({FinalCheckAnnotation.class, FinalCheckLoginAnnotation.class})
@Documented
public @interface EnableFinalSecurityAnnotation {
}
