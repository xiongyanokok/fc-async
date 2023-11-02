package com.xy.async.domain.dao.impl;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.xy.async.domain.dao.AsyncReqDao;
import com.xy.async.domain.entity.AsyncReq;

import cn.hutool.core.collection.CollUtil;

/**
 * 异步执行 dao
 *
 * @author xiongyan
 * @date 2021/01/08
 */
@Repository
public class AsyncReqDaoImpl implements AsyncReqDao {

    @Autowired(required = false)
    private JdbcTemplate asyncJdbcTemplate;

    /**
     * 最大重试执行次数：默认5次
     */
    @Value("${async.exec.count:5}")
    private int execCount;

    /**
     * 每次执行最大查询数量：默认100
     */
    @Value("${async.retry.limit:100}")
    private int retryLimit;

    /**
     * 每次补偿最大查询数量：默认100
     */
    @Value("${async.comp.limit:100}")
    private int compLimit;

    @Override
    public void save(AsyncReq asyncReq) {
        String sql = "insert into async_req(application_name, sign, class_name, method_name, async_type, param_json, remark, exec_status) values (?, ?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        asyncJdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, asyncReq.getApplicationName());
            ps.setString(2, asyncReq.getSign());
            ps.setString(3, asyncReq.getClassName());
            ps.setString(4, asyncReq.getMethodName());
            ps.setString(5, asyncReq.getAsyncType());
            ps.setString(6, asyncReq.getParamJson());
            ps.setString(7, asyncReq.getRemark());
            ps.setInt(8, asyncReq.getExecStatus());
            return ps;
        }, keyHolder);
        asyncReq.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
    }

    @Override
    public void update(AsyncReq asyncReq) {
        String sql = "update async_req set exec_status = ?, exec_count  = exec_count + 1, update_time = ? where id = ?";
        asyncJdbcTemplate.update(sql, asyncReq.getExecStatus(), asyncReq.getUpdateTime(), asyncReq.getId());
    }

    @Override
    public void delete(Long id) {
        String sql = "delete from async_req where id = ?";
        asyncJdbcTemplate.update(sql, id);
    }

    @Override
    public AsyncReq getById(Long id) {
        String sql = "select * from async_req where id = ?";
        List<AsyncReq> list = asyncJdbcTemplate.query(sql, new BeanPropertyRowMapper<>(AsyncReq.class), id);
        return CollUtil.isEmpty(list) ? null : list.get(0);
    }

    @Override
    public List<AsyncReq> listRetry(String applicationName) {
        String sql = "select * from async_req where exec_status = 1 and exec_count < ? and application_name = ? order by id limit ?";
        return asyncJdbcTemplate.query(sql, new BeanPropertyRowMapper<>(AsyncReq.class), execCount, applicationName, retryLimit);
    }

    @Override
    public List<AsyncReq> listComp(String applicationName) {
        String sql = "select * from async_req where exec_status = 0 and exec_count = 0 and date_add(create_time, interval 1 hour) < now() and application_name = ? order by id limit ?";
        return asyncJdbcTemplate.query(sql, new BeanPropertyRowMapper<>(AsyncReq.class), applicationName, compLimit);
    }

    @Override
    public Integer countAsync(String applicationName) {
        String sql = "select count(*) from async_req where exec_status = 1 and exec_count >= ? and application_name = ?";
        return asyncJdbcTemplate.queryForObject(sql, Integer.class, execCount, applicationName);
    }

    @Override
    public List<AsyncReq> listAsync(String applicationName, int pageIndex, int pageSize) {
        String sql = "select * from async_req where exec_status = 1 and exec_count >= ? and application_name = ? order by id limit ?, ?";
        return asyncJdbcTemplate.query(sql, new BeanPropertyRowMapper<>(AsyncReq.class), execCount, applicationName, pageIndex, pageSize);
    }
}
