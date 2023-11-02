package com.xy.async.domain.entity;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 异步执行日志实体类
 *
 * @author xiongyan
 * @date 2021/01/08
 */
@Data
public class AsyncLog implements Serializable {

    private static final long serialVersionUID = 7606071967983038048L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 异步请求ID
     */
    private Long asyncId;

    /**
     * 执行错误信息
     */
    private String errorData;

    /**
     * 创建时间
     */
    private Date createTime;

}
