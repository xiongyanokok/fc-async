package com.xy.async.domain.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.xy.async.domain.dao.AsyncLogDao;
import com.xy.async.domain.entity.AsyncLog;

/**
 * 异步执行日志DAO
 *
 * @author xiongyan
 * @date 2021/01/08
 */
@Repository
public class AsyncLogDaoImpl implements AsyncLogDao {

    @Autowired(required = false)
    private JdbcTemplate asyncJdbcTemplate;

    @Override
    public void save(AsyncLog asyncLog) {
        String sql = "insert into async_log(async_id, error_data, create_time) values (?, ?, ?)";
        asyncJdbcTemplate.update(sql, asyncLog.getAsyncId(), asyncLog.getErrorData(), asyncLog.getCreateTime());
    }

    @Override
    public void delete(Long asyncId) {
        String sql = "delete from async_log where async_id = ?";
        asyncJdbcTemplate.update(sql, asyncId);
    }

    @Override
    public String getErrorData(Long asyncId) {
        String sql = "select error_data from async_log where async_id = ? order by id desc limit 1";
        return asyncJdbcTemplate.queryForObject(sql, String.class, asyncId);
    }
}
