package com.xy.async.mq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.xy.async.biz.AsyncBizService;
import com.xy.async.constant.AsyncConstant;
import com.xy.async.dto.AsyncExecDto;

import lombok.extern.slf4j.Slf4j;

/**
 * 异步执行消费者
 *
 * @author xiongyan
 * @date 2021/01/08
 */
@Slf4j
@Component
public class AsyncConsumer {

    @Autowired
    private AsyncBizService asyncBizService;

    /**
     * 队列名称前缀：默认是应用名称
     */
    @Value("${async.topic:${spring.application.name}}")
    private String asyncTopic;

    /**
     * 消费消息
     *
     * @param asyncExecDto
     * @return
     */
    @KafkaListener(topics = "${async.topic:${spring.application.name}}" + AsyncConstant.QUEUE_SUFFIX,groupId = "${spring.application.name}")
    public void onConsume(AsyncExecDto asyncExecDto) {
        String queueName = asyncTopic + AsyncConstant.QUEUE_SUFFIX;
        try {
            log.info("kafka消息开始消费，queueName：'{}'，message：{}", queueName, asyncExecDto);
            // 执行方法
            asyncBizService.invoke(asyncExecDto);
            log.info("kafka消息消费成功，queueName：'{}'，message：{}", queueName, asyncExecDto);
        } catch (Exception e) {
            log.error("kafka消息消费失败，queueName：'{}'，message：{}", queueName, asyncExecDto, e);
            throw e;
        }
    }

}
