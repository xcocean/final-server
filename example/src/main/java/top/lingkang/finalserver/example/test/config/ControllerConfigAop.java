package top.lingkang.finalserver.example.test.config;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * @author lingkang
 * Created by 2023/1/7
 */
//@Component
//@Aspect
public class ControllerConfigAop {

    // 包下所有controller都进行AOP
    private final static String EXPRESSION = "execution(* top.lingkang.finalserver.example.test.Demo01.*(..))";

    //前置通知
    @Before(EXPRESSION)
    public void beforeAdvice(JoinPoint joinPoint) {
        System.out.println("========== 【Aspectj前置通知】 ==========");
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        //获取参数名称
        String[] parameterNames = methodSignature.getParameterNames();
        System.out.println(Arrays.toString(parameterNames));
    }

    //后置通知：方法正常执行后，有返回值，执行该后置通知：如果该方法执行出现异常，则不执行该后置通知
    @AfterReturning(value = EXPRESSION, returning = "returnVal")
    public void afterReturningAdvice(JoinPoint joinPoint, Object returnVal) {
        System.out.println("========== 【Aspectj后置通知】 ==========");
    }

    //后置通知
    @After(EXPRESSION)
    public void afterAdvice(JoinPoint joinPoint) {
        System.out.println("========== 【Aspectj后置通知】 ==========");
    }

    //环绕通知
    @Around(EXPRESSION)
    public Object aroundAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("##########【环绕通知中的前置通知】##########");
        Object returnVale = joinPoint.proceed();
        System.out.println("##########【环绕通知中的后置通知】##########");
        return returnVale;
    }

    // 异常通知：方法出现异常时，执行该通知
    @AfterThrowing(value = EXPRESSION, throwing = "ex")
    public void throwAdvice(JoinPoint joinPoint, Exception ex) {
        System.out.println("********** 【Aspectj异常通知】执行开始 **********");
        System.out.println("出现异常：" + ex.getMessage());
        System.out.println("********** 【Aspectj异常通知】执行结束 **********");
    }
}
