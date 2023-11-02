package com.xy.async.handler.impl;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import com.xy.async.domain.entity.AsyncReq;
import com.xy.async.enums.AsyncTypeEnum;
import com.xy.async.enums.ExecStatusEnum;
import com.xy.async.handler.context.AsyncContext;

import lombok.extern.slf4j.Slf4j;

/**
 * 先保存数据库再异步消息处理
 *
 * @author xiongyan
 * @date 2021/11/17
 */
@Slf4j
@Component
public class SaveAsyncHandlerService extends AbstractHandlerService {

    @Override
    public List<String> listType() {
        return Collections.singletonList(AsyncTypeEnum.SAVE_ASYNC.name());
    }

    @Override
    public boolean execute(AsyncContext context) {
        // 保存数据库
        AsyncReq asyncReq = this.saveAsyncReq(context.getAsyncExecDto(), ExecStatusEnum.INIT.getStatus());
        if (null == asyncReq) {
            // 降级为仅异步消息处理
            return asyncProducer.send(context.getAsyncExecDto());
        }
        // 放入消息队列（需要数据库ID）
        boolean success = asyncProducer.send(asyncConverter.toAsyncExecDto.apply(asyncReq));
        if (success) {
            return true;
        }
        // 更新状态为失败
        asyncReqService.updateStatus(asyncReq.getId(), ExecStatusEnum.ERROR.getStatus());
        return true;
    }
}
