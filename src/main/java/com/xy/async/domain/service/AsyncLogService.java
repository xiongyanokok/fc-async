package com.xy.async.domain.service;

import com.xy.async.domain.entity.AsyncLog;

/**
 * 异步执行日志接口
 *
 * @author xiongyan
 * @date 2021/01/08
 */
public interface AsyncLogService {

    /**
     * 保存
     *
     * @param asyncLog
     */
    void save(AsyncLog asyncLog);

    /**
     * 删除
     *
     * @param asyncId
     */
    void delete(Long asyncId);

    /**
     * 获取最后一次失败信息
     * 
     * @param asyncId
     * @return
     */
    String getErrorData(Long asyncId);
}
