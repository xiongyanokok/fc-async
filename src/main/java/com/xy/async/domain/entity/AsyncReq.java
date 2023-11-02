package com.xy.async.domain.entity;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 异步执行实体类
 *
 * @author xiongyan
 * @date 2021/01/08
 */
@Data
public class AsyncReq implements Serializable {

    private static final long serialVersionUID = 7606071967983038048L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 应用名称
     */
    private String applicationName;

    /**
     * 方法签名
     */
    private String sign;

    /**
     * 全路径类名称
     */
    private String className;

    /**
     * method名称
     */
    private String methodName;

    /**
     * 异步策略类型
     */
    private String asyncType;

    /**
     * 执行状态 0：未处理 1：处理失败
     */
    private Integer execStatus;

    /**
     * 执行次数
     */
    private Integer execCount;

    /**
     * 参数json字符串
     */
    private String paramJson;

    /**
     * 业务描述
     */
    private String remark;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

}
