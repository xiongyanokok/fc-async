package com.xy.async.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xy.async.biz.AsyncBizService;
import com.xy.async.constant.AsyncConstant;
import com.xy.async.domain.entity.AsyncReq;
import com.xy.async.domain.service.AsyncLogService;
import com.xy.async.domain.service.AsyncReqService;
import com.xy.async.dto.PageInfoDto;

import cn.hutool.core.util.StrUtil;

/**
 * 异步执行API
 *
 * @author xiongyan
 * @date 2021/1/17
 */
@RestController
@RequestMapping("/xy/async")
public class AsyncController {

    @Autowired
    private AsyncReqService asyncReqService;

    @Autowired
    private AsyncLogService asyncLogService;

    @Autowired
    private AsyncBizService asyncBizService;

    @PostMapping(value = "/page")
    public Map<String, Object> page(@RequestBody PageInfoDto<AsyncReq> pageInfo) {
        asyncReqService.listAsyncPage(pageInfo);
        return this.success(pageInfo);
    }

    @PostMapping(value = "/detail/{id}")
    public Map<String, Object> detail(@PathVariable("id") Long id) {
        AsyncReq asyncReq = asyncReqService.getById(id);
        if (null == asyncReq) {
            return this.error("异步任务不存在");
        }
        Map<String, Object> dataMap = new HashMap<>();
        if (StrUtil.isNotEmpty(asyncReq.getParamJson())) {
            asyncReq.setParamJson(asyncReq.getParamJson().replace("\"", ""));
        }
        dataMap.put("req", asyncReq);
        dataMap.put("log", asyncLogService.getErrorData(id));
        return this.success(dataMap);
    }

    @PostMapping(value = "/exec/{id}")
    public Map<String, Object> exec(@PathVariable("id") Long id) {
        AsyncReq asyncReq = asyncReqService.getById(id);
        if (null == asyncReq) {
            return this.error("异步任务不存在");
        }
        if (asyncBizService.invoke(asyncReq)) {
            return this.success("执行成功");
        } else {
            return this.error("执行失败");
        }
    }

    @PostMapping(value = "/delete/{id}")
    public Map<String, Object> delete(@PathVariable("id") Long id) {
        asyncReqService.delete(id);
        asyncLogService.delete(id);
        return this.success("删除成功");
    }

    private Map<String, Object> success(Object data) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", AsyncConstant.SUCCESS);
        map.put("data", data);
        return map;
    }

    private Map<String, Object> error(String msg) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", AsyncConstant.ERROR);
        map.put("msg", msg);
        return map;
    }

}
