package com.xy.async.handler.impl;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.xy.async.biz.AsyncBizService;
import com.xy.async.enums.AsyncTypeEnum;
import com.xy.async.handler.context.AsyncContext;

import lombok.extern.slf4j.Slf4j;

/**
 * 异步线程处理
 *
 * @author xiongyan
 * @date 2021/11/17
 */
@Slf4j
@Component
public class ThreadHandlerService extends AbstractHandlerService {

    @Autowired(required = false)
    private Executor asyncExecute;

    @Autowired
    private AsyncBizService asyncBizService;

    @Override
    public List<String> listType() {
        return Collections.singletonList(AsyncTypeEnum.THREAD.name());
    }

    @Override
    public boolean execute(AsyncContext context) {
        asyncExecute.execute(() -> asyncBizService.invoke(context.getAsyncExecDto()));
        return true;
    }
}
