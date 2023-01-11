package top.lingkang.finalserver.server.web.security.annotation.impl;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import top.lingkang.finalserver.server.web.http.FinalServerContext;
import top.lingkang.finalserver.server.web.security.annotation.FinalCheck;
import top.lingkang.finalserver.server.web.security.config.FinalSecurityConfiguration;
import top.lingkang.finalserver.server.web.security.constants.FinalConstants;
import top.lingkang.finalserver.server.web.security.error.FinalNotLoginException;
import top.lingkang.finalserver.server.web.security.http.FinalSecurityHolder;
import top.lingkang.finalserver.server.web.security.utils.AuthUtils;

/**
 * @author lingkang
 * Created by 2022/1/11
 * @since 1.0.0
 */
@Aspect
public class FinalCheckAnnotation {
    @Autowired(required = false)
    private FinalSecurityHolder securityHolder;
    @Autowired(required = false)
    private FinalSecurityConfiguration finalSecurityConfiguration;

    @Around("@within(top.lingkang.finalserver.server.web.security.annotation.FinalCheck) || @annotation(top.lingkang.finalserver.server.web.security.annotation.FinalCheckLogin)")
    public Object method(ProceedingJoinPoint joinPoint) throws Throwable {
        if (finalSecurityConfiguration.getProperties()
                .getCheckPathCache().getExcludePath()
                .contains(FinalServerContext.currentContext().getRequest().getPath())) {
            return joinPoint.proceed();
        }

        if (!securityHolder.isLogin()) {
            throw new FinalNotLoginException(FinalConstants.NOT_LOGIN_MSG);
        }

        FinalCheck clazz = joinPoint.getTarget().getClass().getAnnotation(FinalCheck.class);
        if (clazz != null) {
            check(clazz);
        }

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        FinalCheck method = signature.getMethod().getAnnotation(FinalCheck.class);
        if (method != null) {
            check(method);
        }

        return joinPoint.proceed();
    }

    private void check(FinalCheck check) {
        if (check.anyRole().length != 0)
            AuthUtils.checkRole(check.anyRole(), securityHolder.getRole());
        if (check.andRole().length != 0)
            AuthUtils.checkAndRole(check.andRole(), securityHolder.getRole());
    }
}
