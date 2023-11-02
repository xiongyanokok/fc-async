package com.xy.async.handler.impl;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import com.xy.async.enums.AsyncTypeEnum;
import com.xy.async.handler.context.AsyncContext;

/**
 * 仅异步消息处理
 *
 * @author xiongyan
 * @date 2021/11/17
 */
@Component
public class AsyncHandlerService extends AbstractHandlerService {

    @Override
    public List<String> listType() {
        return Collections.singletonList(AsyncTypeEnum.ASYNC.name());
    }

    @Override
    public boolean execute(AsyncContext context) {
        // 放入消息队列
        return asyncProducer.send(context.getAsyncExecDto());
    }
}
