package com.fc.async.handler;

import com.fc.async.handler.context.AsyncContext;
import com.fc.async.strategy.StrategyService;

/**
 * 异步执行接口
 *
 * @author xiongyan
 * @date 2021/11/17
 */
public interface HandlerService extends StrategyService<AsyncContext> {

    /**
     * 执行异步策略
     * 
     * @param context
     * @return
     */
    boolean execute(AsyncContext context);
}
