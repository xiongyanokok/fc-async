package com.xy.async.enums;

import lombok.Getter;

/**
 * 执行状态枚举
 *
 * @author xiongyan
 * @date 2021/01/02
 */
@Getter
public enum ExecStatusEnum {

    /**
     * 初始化
     */
    INIT(0, "初始化"),

    /**
     * 执行失败
     */
    ERROR(1, "执行失败"),

    /**
     * 执行成功
     */
    SUCCESS(2, "执行成功");

    /**
     * 类型
     */
    private final int status;

    /**
     * 名称
     */
    private final String name;

    ExecStatusEnum(int status, String name) {
        this.status = status;
        this.name = name;
    }
}
