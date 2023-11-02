package com.xy.async.domain.dao;

import java.util.List;

import com.xy.async.domain.entity.AsyncReq;

/**
 * 异步执行 dao
 *
 * @author xiongyan
 * @date 2021/01/08
 */
public interface AsyncReqDao {

    /**
     * 保存
     *
     * @param asyncReq
     */
    void save(AsyncReq asyncReq);

    /**
     * 更新
     *
     * @param asyncReq
     */
    void update(AsyncReq asyncReq);

    /**
     * 删除
     *
     * @param id
     */
    void delete(Long id);

    /**
     * 根据ID查询
     *
     * @param id
     * @return
     */
    AsyncReq getById(Long id);

    /**
     * 自动重试
     * 
     * @param applicationName
     * @return
     */
    List<AsyncReq> listRetry(String applicationName);

    /**
     * 自动补偿
     * 
     * @param applicationName
     * @return
     */
    List<AsyncReq> listComp(String applicationName);

    /**
     * 人工执行总数量
     *
     * @param applicationName
     * @return
     */
    Integer countAsync(String applicationName);

    /**
     * 人工执行
     *
     * @param applicationName
     * @param pageIndex
     * @param pageSize
     * @return
     */
    List<AsyncReq> listAsync(String applicationName, int pageIndex, int pageSize);
}
