package com.xy.async.biz;

import com.xy.async.domain.entity.AsyncReq;
import com.xy.async.dto.AsyncExecDto;

/**
 * 异步执行接口
 *
 * @author xiongyan
 * @date 2021/01/08
 */
public interface AsyncBizService {

    /**
     * 执行方法
     *
     * @param asyncReq
     * @return
     */
    boolean invoke(AsyncReq asyncReq);

    /**
     * 执行方法
     *
     * @param asyncExecDto
     * @return
     */
    boolean invoke(AsyncExecDto asyncExecDto);

}
