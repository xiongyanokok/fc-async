package com.xy.async.config;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.util.Assert;

import com.xy.async.handler.HandlerService;
import com.xy.async.handler.context.AsyncContext;
import com.xy.async.strategy.StrategyFactory;

import lombok.extern.slf4j.Slf4j;

/**
 * 事件监听
 *
 * @author xiongyan
 * @date 2022/3/2
 */
@Slf4j
@Component
public class AsyncListener {

    /**
     * 处理事件 <br>
     * fallbackExecution=true 没有事务正在运行，依然处理事件 <br>
     * TransactionPhase.AFTER_COMPLETION 事务提交，事务回滚都处理事件
     * 
     * @param context
     */
    @TransactionalEventListener(fallbackExecution = true, phase = TransactionPhase.AFTER_COMPLETION)
    public void asyncHandler(AsyncContext context) {
        HandlerService handlerService = StrategyFactory.doStrategy(context.getAsyncExec().type().name(), HandlerService.class);
        Assert.notNull(handlerService, "异步执行策略不存在");
        handlerService.handle(context);
    }

}
