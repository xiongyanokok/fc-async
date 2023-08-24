package com.fc.async.handler.context;

import org.aspectj.lang.ProceedingJoinPoint;

import com.fc.async.annotation.AsyncExec;
import com.fc.async.dto.AsyncExecDto;
import com.fc.async.strategy.context.StrategyContext;

import lombok.Data;

/**
 * AsyncContext
 *
 * @author xiongyan
 * @date 2021/11/17
 */
@Data
public class AsyncContext extends StrategyContext {

    private static final long serialVersionUID = 1L;

    /**
     * 切面方法
     */
    private ProceedingJoinPoint joinPoint;

    /**
     * 异步执行策略
     */
    private AsyncExec asyncExec;

    /**
     * 异步执行数据
     */
    private AsyncExecDto asyncExecDto;

}
