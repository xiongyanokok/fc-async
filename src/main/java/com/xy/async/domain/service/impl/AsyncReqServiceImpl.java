package com.xy.async.domain.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.xy.async.domain.dao.AsyncReqDao;
import com.xy.async.domain.entity.AsyncReq;
import com.xy.async.domain.service.AsyncReqService;
import com.xy.async.dto.PageInfoDto;

/**
 * 异步执行接口实现
 *
 * @author xiongyan
 * @date 2021/01/08
 */
@Service
public class AsyncReqServiceImpl implements AsyncReqService {

    @Autowired(required = false)
    private AsyncReqDao asyncReqDao;

    @Value("${spring.application.name}")
    private String applicationName;

    @Override
    public void save(AsyncReq asyncReq) {
        asyncReq.setCreateTime(new Date());
        asyncReq.setUpdateTime(new Date());
        asyncReqDao.save(asyncReq);
    }

    @Override
    public void updateStatus(Long id, Integer execStatus) {
        AsyncReq update = new AsyncReq();
        update.setId(id);
        update.setExecStatus(execStatus);
        update.setUpdateTime(new Date());
        asyncReqDao.update(update);
    }

    @Override
    public void delete(Long id) {
        asyncReqDao.delete(id);
    }

    @Override
    public AsyncReq getById(Long id) {
        return asyncReqDao.getById(id);
    }

    @Override
    public List<AsyncReq> listRetry() {
        return asyncReqDao.listRetry(applicationName);
    }

    @Override
    public List<AsyncReq> listComp() {
        return asyncReqDao.listComp(applicationName);
    }

    @Override
    public void listAsyncPage(PageInfoDto<AsyncReq> pageInfo) {
        Integer total = asyncReqDao.countAsync(applicationName);
        if (null == total || total == 0) {
            return;
        }
        List<AsyncReq> list = asyncReqDao.listAsync(applicationName, (pageInfo.getPageNum() - 1) * pageInfo.getPageSize(), pageInfo.getPageSize());
        pageInfo.setTotal(total);
        pageInfo.setList(list);
    }

}