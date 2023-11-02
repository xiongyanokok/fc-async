package com.xy.async.enums;

import lombok.Getter;

/**
 * 异步执行枚举
 *
 * @author xiongyan
 * @date 2021/01/02
 */
@Getter
public enum AsyncTypeEnum {

    /**
     * 先保存数据库再异步消息处理
     */
    SAVE_ASYNC("先保存数据库再异步消息处理"),

    /**
     * 先同步处理失败再保存数据库
     */
    SYNC_SAVE("先同步处理失败再保存数据库"),

    /**
     * 先异步消息处理失败再保存数据库
     */
    ASYNC_SAVE("先异步消息处理失败再保存数据库"),

    /**
     * 仅异步消息处理
     */
    ASYNC("仅异步消息处理"),

    /**
     * 仅异步线程处理
     */
    THREAD("仅异步线程处理");

    /**
     * 描述
     */
    private final String desc;

    AsyncTypeEnum(String desc) {
        this.desc = desc;
    }

    public static String getDesc(String type) {
        for (AsyncTypeEnum typeEnum : AsyncTypeEnum.values()) {
            if (typeEnum.name().equals(type)) {
                return typeEnum.getDesc();
            }
        }
        return null;
    }

}
