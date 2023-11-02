package com.xy.async.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.xy.async.annotation.AsyncExec;
import com.xy.async.constant.AsyncConstant;
import com.xy.async.handler.context.AsyncContext;

import lombok.extern.slf4j.Slf4j;

/**
 * 异步执行切面
 *
 * @author xiongyan
 * @date 2021/01/02
 */
@Slf4j
@Aspect
@Component
@ConditionalOnProperty(prefix = "async", value = "enabled", havingValue = "true")
public class AsyncAspect {

    @Autowired
    private ApplicationEventPublisher publisher;

    @Around("@annotation(asyncExec)")
    public Object proceed(ProceedingJoinPoint joinPoint, AsyncExec asyncExec) throws Throwable {
        if (AsyncConstant.PUBLISH_EVENT.get()) {
            try {
                // 直接执行
                return joinPoint.proceed();
            } finally {
                AsyncConstant.PUBLISH_EVENT.remove();
            }
        } else {
            AsyncContext context = new AsyncContext();
            context.setJoinPoint(joinPoint);
            context.setAsyncExec(asyncExec);
            // 发布事件
            publisher.publishEvent(context);
            log.info("异步执行事件发布成功，策略类型：{}，业务描述：{}", asyncExec.type(), asyncExec.remark());

            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Class<?> returnType = signature.getMethod().getReturnType();
            if (returnType != Void.TYPE && returnType.isPrimitive()) {
                // 8种基本类型需特殊处理（byte、short、char、int、long、float、double、boolean）
                return returnType == Boolean.TYPE ? Boolean.TRUE : 1;
            }
            return null;
        }
    }

}
