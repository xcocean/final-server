package top.lingkang.finalserver.server.web.security.annotation.impl;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import top.lingkang.finalserver.server.web.http.FinalServerContext;
import top.lingkang.finalserver.server.web.security.config.FinalSecurityConfiguration;
import top.lingkang.finalserver.server.web.security.constants.FinalConstants;
import top.lingkang.finalserver.server.web.security.error.FinalPermissionException;
import top.lingkang.finalserver.server.web.security.http.FinalSecurityHolder;

/**
 * @author lingkang
 * Created by 2022/1/11
 * @since 1.0.0
 */
@Aspect
public class FinalCheckLoginAnnotation {
    @Autowired(required = false)
    private FinalSecurityHolder securityHolder;
    @Autowired(required = false)
    private FinalSecurityConfiguration finalSecurityConfiguration;

    @Around("@within(top.lingkang.finalserver.server.web.security.annotation.FinalCheckLogin) || @annotation(top.lingkang.finalserver.server.web.security.annotation.FinalCheckLogin)")
    public Object before(ProceedingJoinPoint joinPoint) throws Throwable {
        if (!finalSecurityConfiguration.getProperties().getCheckPathCache().getExcludePath().contains(FinalServerContext.currentContext().getRequest().getPath())) {
            if (!securityHolder.isLogin()) {
                throw new FinalPermissionException(FinalConstants.UNAUTHORIZED_MSG);
            }
        }
        return joinPoint.proceed();
    }
}
