package com.hanhou.aspect;


import com.hanhou.annotation.DesensitizationMethod;
import com.hanhou.utils.DesensitizationUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 脱敏切面
 *
 * @author chenguoku
 * @version 0.0.1
 * @ClassName DeSensitiveAspect.java
 * @createTime 2022年03月10日
 */
@Aspect
@Component
public class DesensitizationAspect {

    @Pointcut("@annotation(com.hanhou.annotation.DesensitizationMethod) || @within(com.hanhou.annotation.DesensitizationMethod)")
    public void desensitization() {
    }

    /**
     * 方法拦截，字段脱敏
     */
    @Around("desensitization()")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        // result 为方法的返回值
        Object result = pjp.proceed();

        if (result == null) {
            return null;
        }

        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        DesensitizationMethod annotation = method.getAnnotation(DesensitizationMethod.class);
        if (annotation != null) {
            DesensitizationUtils.format(result);
        }
        return result;
    }

}
