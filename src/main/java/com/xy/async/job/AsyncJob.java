package com.xy.async.job;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.xxl.job.core.handler.annotation.XxlJob;
import com.xy.async.biz.AsyncBizService;
import com.xy.async.domain.entity.AsyncReq;
import com.xy.async.domain.service.AsyncReqService;

import lombok.extern.slf4j.Slf4j;

/**
 * 异步执行失败重试定时任务
 *
 * @author xiongyan
 * @date 2021/01/02
 */
@Slf4j
@Component
public class AsyncJob {

    @Autowired
    private AsyncReqService asyncReqService;

    @Autowired
    private AsyncBizService asyncBizService;

    /**
     * name = "AsyncJob"
     * cron = "0 0/2 * * * ?"
     * description = "异步执行失败重试定时任务"
     */
    @XxlJob("AsyncJob")
    public void execute() {
        // 任务开始时间
        long start = System.currentTimeMillis();
        try {
            log.info("异步重试定时任务执行开始......");
            // 执行任务
            List<AsyncReq> asyncReqList = asyncReqService.listRetry();
            if (CollectionUtils.isEmpty(asyncReqList)) {
                return;
            }
            for (AsyncReq asyncReq : asyncReqList) {
                asyncBizService.invoke(asyncReq);
            }
        } catch (Throwable e) {
            log.error("异步重试定时任务执行失败......", e);
        } finally {
            // 任务结束时间
            long end = System.currentTimeMillis();
            log.info("异步重试定时任务执行结束...... 用时：{}毫秒", end - start);
        }
    }

}
