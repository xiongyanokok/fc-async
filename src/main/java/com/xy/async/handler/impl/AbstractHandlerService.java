package com.xy.async.handler.impl;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.xy.async.annotation.AsyncExec;
import com.xy.async.config.AsyncConverter;
import com.xy.async.config.AsyncProxy;
import com.xy.async.constant.AsyncConstant;
import com.xy.async.domain.entity.AsyncReq;
import com.xy.async.domain.service.AsyncReqService;
import com.xy.async.dto.AsyncExecDto;
import com.xy.async.handler.HandlerService;
import com.xy.async.handler.context.AsyncContext;
import com.xy.async.mq.AsyncProducer;
import com.xy.async.util.JacksonUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * AbstractHandlerService
 *
 * @author xiongyan
 * @date 2021/11/17
 */
@Slf4j
public abstract class AbstractHandlerService implements HandlerService {

    @Autowired
    private AsyncProxy asyncProxy;

    @Autowired
    protected AsyncConverter asyncConverter;

    @Autowired
    protected AsyncProducer asyncProducer;

    @Autowired
    protected AsyncReqService asyncReqService;

    @Value("${spring.application.name}")
    private String applicationName;

    @Override
    public boolean handle(AsyncContext context) {
        // 异步执行数据
        AsyncExecDto asyncExecDto = this.getAsyncExecDto(context);
        context.setAsyncExecDto(asyncExecDto);
        // 执行异步策略
        boolean success = this.execute(context);
        if (!success) {
            // 最终兜底方案直接执行
            try {
                context.getJoinPoint().proceed();
            } catch (Throwable e) {
                log.error("兜底方案依然执行失败：{}", asyncExecDto, e);
                log.error("人工处理，queue：{}，message：{}", applicationName + AsyncConstant.QUEUE_SUFFIX, JacksonUtil.toJsonString(asyncExecDto));
            }
        }
        return true;
    }

    /**
     * 保存数据库
     *
     * @param asyncExecDto
     * @param execStatus
     * @return
     */
    public AsyncReq saveAsyncReq(AsyncExecDto asyncExecDto, Integer execStatus) {
        AsyncReq asyncReq = asyncConverter.toAsyncReq.apply(asyncExecDto);
        try {
            asyncReq.setExecStatus(execStatus);
            asyncReqService.save(asyncReq);
            log.info("异步执行保存数据库成功：{}", asyncReq);
            return asyncReq;
        } catch (Exception e) {
            log.error("异步执行保存数据库失败：{}", asyncReq, e);
            return null;
        }
    }

    /**
     * AsyncExecDto
     * 
     * @param context
     * @return
     */
    private AsyncExecDto getAsyncExecDto(AsyncContext context) {
        ProceedingJoinPoint joinPoint = context.getJoinPoint();
        AsyncExec asyncExec = context.getAsyncExec();
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        AsyncExecDto asyncExecDto = new AsyncExecDto();
        asyncExecDto.setApplicationName(applicationName);
        asyncExecDto.setSign(asyncProxy.getAsyncMethodKey(joinPoint.getTarget(), methodSignature.getMethod()));
        asyncExecDto.setClassName(joinPoint.getTarget().getClass().getName());
        asyncExecDto.setMethodName(methodSignature.getMethod().getName());
        asyncExecDto.setAsyncType(asyncExec.type().name());
        asyncExecDto.setParamJson(JacksonUtil.toJsonString(joinPoint.getArgs()));
        asyncExecDto.setRemark(asyncExec.remark());
        return asyncExecDto;
    }

}
