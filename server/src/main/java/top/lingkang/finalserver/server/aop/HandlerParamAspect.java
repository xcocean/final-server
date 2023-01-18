package top.lingkang.finalserver.server.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import top.lingkang.finalserver.server.annotation.*;
import top.lingkang.finalserver.server.web.handler.MethodHandlerParam;
import top.lingkang.finalserver.server.web.http.FinalServerContext;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * @author lingkang
 * Created by 2023/1/18
 * @serial 1.0.0
 */
@Aspect
@Component
public class HandlerParamAspect {
    private MethodHandlerParam handlerParam = new MethodHandlerParam();

    @Around("@within(top.lingkang.finalserver.server.annotation.Controller)")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        return setParam(joinPoint);
    }

    @Around("@within(org.springframework.stereotype.Controller)")
    public Object doAroundSpring(ProceedingJoinPoint joinPoint) throws Throwable {
        return setParam(joinPoint);
    }

    private static final Set<Method> cache = new HashSet<>();
    private static final Set<Method> cache0 = new HashSet<>();

    private Object setParam(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        if (cache.contains(method)) {
            return handler(joinPoint, methodSignature);
        } else if (cache0.contains(method)) {
            return joinPoint.proceed();
        } else if (method.getAnnotations().length != 0) {
            if (method.isAnnotationPresent(GET.class) || method.isAnnotationPresent(POST.class) ||
                    method.isAnnotationPresent(DELETE.class) || method.isAnnotationPresent(PUT.class)
                    || method.isAnnotationPresent(RequestMapping.class)) {
                cache.add(method);
                return handler(joinPoint, methodSignature);
            }
        }
        cache0.add(method);
        return joinPoint.proceed();
    }

    private Object handler(ProceedingJoinPoint joinPoint, MethodSignature methodSignature) throws Throwable {
        //获取参数名称
        String[] parameterNames = methodSignature.getParameterNames();
        Object[] args = new Object[parameterNames.length];
        for (int i = 0; i < parameterNames.length; i++) {
            args[i] = handlerParam.match(
                    parameterNames[i],
                    methodSignature.getParameterTypes()[i],
                    FinalServerContext.currentContext());
        }
        return joinPoint.proceed(args);
    }
}
