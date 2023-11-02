package com.xy.async.handler.impl;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.xy.async.config.SpringBeanConfig;
import com.xy.async.domain.entity.AsyncReq;
import com.xy.async.enums.AsyncTypeEnum;
import com.xy.async.enums.ExecStatusEnum;
import com.xy.async.handler.context.AsyncContext;

import lombok.extern.slf4j.Slf4j;

/**
 * 先同步处理失败再保存数据库
 *
 * @author xiongyan
 * @date 2021/11/17
 */
@Slf4j
@Component
public class SyncSaveHandlerService extends AbstractHandlerService {

    @Override
    public List<String> listType() {
        return Collections.singletonList(AsyncTypeEnum.SYNC_SAVE.name());
    }

    @Override
    public boolean execute(AsyncContext context) {
        // 同步处理，由于不能影响主线程事务，但是异步方法上面又有事务所有需要开启新事物
        TransactionStatus status = null;
        PlatformTransactionManager transactionManager = null;
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
            definition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
            transactionManager = SpringBeanConfig.getBean(PlatformTransactionManager.class);
            status = transactionManager.getTransaction(definition);
        }
        try {
            // 同步处理
            context.getJoinPoint().proceed();
            if (null != status) {
                transactionManager.commit(status);
            }
        } catch (Throwable e) {
            log.warn("先同步处理失败：{}", context.getAsyncExecDto(), e);
            if (null != status) {
                transactionManager.rollback(status);
            }
            // 保存数据库
            AsyncReq asyncReq = this.saveAsyncReq(context.getAsyncExecDto(), ExecStatusEnum.ERROR.getStatus());
            if (null == asyncReq) {
                // 降级为仅异步消息处理
                asyncProducer.send(context.getAsyncExecDto());
            }
        }
        return true;
    }

}
