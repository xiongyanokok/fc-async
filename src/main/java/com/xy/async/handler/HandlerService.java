package com.xy.async.handler;

import com.xy.async.handler.context.AsyncContext;
import com.xy.async.strategy.StrategyService;

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
