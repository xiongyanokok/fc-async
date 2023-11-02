package com.xy.async.constant;

/**
 * 常量
 *
 * @author xiongyan
 * @date 2020/5/21
 */
public class AsyncConstant {

    /**
     * 执行代理方法防止死循环
     */
    public static final ThreadLocal<Boolean> PUBLISH_EVENT = ThreadLocal.withInitial(() -> false);

    /**
     * 成功
     */
    public static final int SUCCESS = 1;

    /**
     * 失败
     */
    public static final int ERROR = 0;

    /**
     * 登录
     */
    public static final int LOGIN = -1;

    /**
     * 队列后缀
     */
    public static final String QUEUE_SUFFIX = "_async_queue";

}
