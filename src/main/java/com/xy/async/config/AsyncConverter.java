package com.xy.async.config;

import java.util.function.Function;

import org.springframework.stereotype.Component;

import com.xy.async.domain.entity.AsyncReq;
import com.xy.async.dto.AsyncExecDto;

/**
 * 数据转换
 *
 * @author xiongyan
 * @date 2021/01/02
 */
@Component
public class AsyncConverter {

    /**
     * AsyncReq -> AsyncExecDto
     */
    public Function<AsyncReq, AsyncExecDto> toAsyncExecDto = (asyncReq) -> {
        if (null == asyncReq) {
            return null;
        }
        AsyncExecDto asyncExecDto = new AsyncExecDto();
        asyncExecDto.setId(asyncReq.getId());
        asyncExecDto.setApplicationName(asyncReq.getApplicationName());
        asyncExecDto.setSign(asyncReq.getSign());
        asyncExecDto.setClassName(asyncReq.getClassName());
        asyncExecDto.setMethodName(asyncReq.getMethodName());
        asyncExecDto.setAsyncType(asyncReq.getAsyncType());
        asyncExecDto.setParamJson(asyncReq.getParamJson());
        asyncExecDto.setRemark(asyncReq.getRemark());
        return asyncExecDto;
    };

    /**
     * AsyncExecDto -> AsyncReq
     */
    public Function<AsyncExecDto, AsyncReq> toAsyncReq = (asyncExecDto) -> {
        if (null == asyncExecDto) {
            return null;
        }
        AsyncReq asyncReq = new AsyncReq();
        asyncReq.setApplicationName(asyncExecDto.getApplicationName());
        asyncReq.setSign(asyncExecDto.getSign());
        asyncReq.setClassName(asyncExecDto.getClassName());
        asyncReq.setMethodName(asyncExecDto.getMethodName());
        asyncReq.setAsyncType(asyncExecDto.getAsyncType());
        asyncReq.setParamJson(asyncExecDto.getParamJson());
        asyncReq.setRemark(asyncExecDto.getRemark());
        return asyncReq;
    };

}
