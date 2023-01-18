package top.lingkang.finalserver.example.test04;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

/**
 * @author lingkang
 * Created by 2023/1/7
 */
//@Component
//@Aspect
public class ControllerConfigAop {

    // 包下所有controller都进行AOP
    private final static String EXPRESSION = "execution(* top.lingkang.finalserver.example.test04.Test04WebApp.*(..))";

    public ControllerConfigAop() {
        System.out.println("aop");
    }

    //环绕通知
     @Around("@annotation(top.lingkang.finalserver.server.annotation.Controller)")
    //@Around(EXPRESSION)
    public Object aroundAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("##########【环绕通知中的前置通知】##########");
        Object returnVale = joinPoint.proceed();
        System.out.println("##########【环绕通知中的后置通知】##########");
        return returnVale;
    }

}
