package com.xy.async.biz.impl;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import com.xy.async.biz.AsyncBizService;
import com.xy.async.config.AsyncConverter;
import com.xy.async.config.AsyncProxy;
import com.xy.async.constant.AsyncConstant;
import com.xy.async.domain.entity.AsyncLog;
import com.xy.async.domain.entity.AsyncReq;
import com.xy.async.domain.service.AsyncLogService;
import com.xy.async.domain.service.AsyncReqService;
import com.xy.async.dto.AsyncExecDto;
import com.xy.async.dto.ProxyMethodDto;
import com.xy.async.enums.AsyncTypeEnum;
import com.xy.async.enums.ExecStatusEnum;
import com.xy.async.util.JacksonUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 异步执行实现
 *
 * @author xiongyan
 * @date 2021/01/08
 */
@Slf4j
@Component
public class AsyncBizServiceImpl implements AsyncBizService  {

    @Autowired
    private AsyncReqService asyncReqService;

    @Autowired
    private AsyncLogService asyncLogService;

    @Autowired
    private AsyncConverter asyncConverter;

    @Autowired
    private AsyncProxy asyncProxy;

    /**
     * 执行成功是否删除：默认是
     */
    @Value("${async.exec.deleted:true}")
    private boolean deleted;

    /**
     * 最大重试执行次数：默认5次
     */
    @Value("${async.exec.count:5}")
    private int execCount;

    /**
     * 执行方法
     *
     * @param asyncReq
     * @return
     */
    @Override
    public boolean invoke(AsyncReq asyncReq) {
        return this.invoke(asyncConverter.toAsyncExecDto.apply(asyncReq));
    }

    /**
     * 执行方法
     *
     * @param asyncExecDto
     * @return
     */
    @Override
    public boolean invoke(AsyncExecDto asyncExecDto) {
        if (null == asyncExecDto) {
            return true;
        }
        // 标记
        AsyncConstant.PUBLISH_EVENT.set(Boolean.TRUE);
        // 获取执行的类和方法
        ProxyMethodDto proxyMethodDto = asyncProxy.getProxyMethod(asyncExecDto.getSign());
        if (null == proxyMethodDto) {
            log.warn("异步执行代理类方法不存在：{}", asyncExecDto);
            return true;
        }

        if (null == asyncExecDto.getId()) {
            // 直接执行
            return this.execute(proxyMethodDto, asyncExecDto);
        } else {
            // 补偿执行
            return this.recoupExecute(proxyMethodDto, asyncExecDto);
        }
    }

    /**
     * 直接执行
     *
     * @param proxyMethodDto
     * @param asyncExecDto
     * @return
     */
    private boolean execute(ProxyMethodDto proxyMethodDto, AsyncExecDto asyncExecDto) {
        try {
            // 执行异步方法
            this.invokeMethod(proxyMethodDto, asyncExecDto);
            return true;
        } catch (Exception e) {
            if (AsyncTypeEnum.ASYNC.name().equals(asyncExecDto.getAsyncType()) || AsyncTypeEnum.THREAD.name().equals(asyncExecDto.getAsyncType())) {
                // 异步消息和异步线程 执行失败 不保存数据库
                log.error("【{}】执行失败：{}", AsyncTypeEnum.getDesc(asyncExecDto.getAsyncType()), asyncExecDto, e);
            } else {
                // 保存异步执行请求
                this.saveAsyncReq(asyncExecDto);
            }
            return false;
        }
    }

    /**
     * 补偿执行
     *
     * @param proxyMethodDto
     * @param asyncExecDto
     * @return
     */
    private boolean recoupExecute(ProxyMethodDto proxyMethodDto, AsyncExecDto asyncExecDto) {
        AsyncReq asyncReq = asyncReqService.getById(asyncExecDto.getId());
        if (null == asyncReq) {
            return true;
        }
        try {
            // 执行异步方法
            this.invokeMethod(proxyMethodDto, asyncExecDto);
            // 更新执行结果
            this.updateAsyncReq(asyncReq);
            return true;
        } catch (Exception e) {
            if (asyncReq.getExecCount() + 1 >= execCount) {
                log.error("异步执行方法失败超过{}次：{}", execCount, asyncExecDto, e);
            }
            // 执行失败更新执行次数且记录失败日志
            this.saveAsyncLog(asyncReq, e);
            return false;
        }
    }

    /**
     * 反射执行异步方法
     * 
     * @param proxyMethodDto
     * @param asyncExecDto
     */
    private void invokeMethod(ProxyMethodDto proxyMethodDto, AsyncExecDto asyncExecDto) {
        log.info("异步执行方法开始：{}", asyncExecDto);
        // 获取参数类型
        Object[] paramTypes = this.getParamType(proxyMethodDto.getMethod(), asyncExecDto.getParamJson());
        // 执行方法
        ReflectionUtils.invokeMethod(proxyMethodDto.getMethod(), proxyMethodDto.getBean(), paramTypes);
        log.info("异步执行方法成功：{}", asyncExecDto);
    }

    /**
     * 获取方法参数
     *
     * @param method
     * @param data
     * @return
     */
    private Object[] getParamType(Method method, String data) {
        Type[] types = method.getGenericParameterTypes();
        if (types.length == 0) {
            return null;
        }
        return JacksonUtil.toObjects(data, types);
    }

    /**
     * 保存异步执行请求
     *
     * @param asyncExecDto
     */
    private void saveAsyncReq(AsyncExecDto asyncExecDto) {
        AsyncReq asyncReq = asyncConverter.toAsyncReq.apply(asyncExecDto);
        asyncReq.setExecStatus(ExecStatusEnum.ERROR.getStatus());
        asyncReqService.save(asyncReq);
        log.info("处理失败后保存数据库成功：{}", asyncReq);
    }

    /**
     * 执行失败更新执行次数且记录失败日志
     *
     * @param asyncReq
     * @param e
     */
    private void saveAsyncLog(AsyncReq asyncReq, Exception e) {
        // 更新状态为失败
        asyncReqService.updateStatus(asyncReq.getId(), ExecStatusEnum.ERROR.getStatus());
        // 保存执行失败日志
        AsyncLog asyncLog = new AsyncLog();
        asyncLog.setAsyncId(asyncReq.getId());
        asyncLog.setErrorData(ExceptionUtils.getStackTrace(e));
        asyncLogService.save(asyncLog);
        log.info("处理失败后保存失败日志成功：{}", asyncReq);
    }

    /**
     * 更新异步执行请求
     *
     * @param asyncReq
     */
    private void updateAsyncReq(AsyncReq asyncReq) {
        if (deleted) {
            // 删除异步执行请求
            asyncReqService.delete(asyncReq.getId());
        } else {
            // 更新状态为成功
            asyncReqService.updateStatus(asyncReq.getId(), ExecStatusEnum.SUCCESS.getStatus());
        }
        if (asyncReq.getExecStatus() == ExecStatusEnum.ERROR.getStatus()) {
            // 删除异步执行日志
            asyncLogService.delete(asyncReq.getId());
        }
    }

}
