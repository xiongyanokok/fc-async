package com.xy.async.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.xy.async.enums.AsyncTypeEnum;

/**
 * 异步执行注解
 *
 * @author xiongyan
 * @date 2021/01/02
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AsyncExec {

    /**
     * 异步执行策略
     * 
     * @return
     */
    AsyncTypeEnum type();

    /**
     * 延迟处理时间
     * 
     * @return
     */
    int delayTime() default 0;

    /**
     * 业务描述
     * 
     * @return
     */
    String remark();
}
