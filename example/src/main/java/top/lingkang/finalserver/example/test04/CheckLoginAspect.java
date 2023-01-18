package top.lingkang.finalserver.example.test04;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author 绫小路
 * @date 2021/7/20 14:36
 * @description
 */
//@Aspect
//@Component
public class CheckLoginAspect {

    @Around("@annotation(top.lingkang.finalserver.example.test04.CheckLogin)")
    public Object before(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("CheckLogin");
        return joinPoint.proceed();
    }

    private Optional<CheckLogin> getAnnotation(ProceedingJoinPoint pjp) throws SecurityException {
        return Optional.ofNullable(((MethodSignature) pjp.getSignature()).getMethod().getAnnotation(CheckLogin.class));
    }
}
