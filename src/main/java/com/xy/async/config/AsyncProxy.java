package com.xy.async.config;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.xy.async.dto.ProxyMethodDto;
import com.xy.async.util.Md5Util;

/**
 * 代理类
 *
 * @author xiongyan
 * @date 2021/1/14
 */
@Component
public class AsyncProxy {

    /**
     * 代理类方法
     */
    private static final Map<String, ProxyMethodDto> PROXY_METHOD_MAP = new ConcurrentHashMap<>();

    /**
     * 设置代理方法
     * 
     * @param key
     * @param proxyMethodDto
     */
    public void setProxyMethod(String key, ProxyMethodDto proxyMethodDto) {
        AsyncProxy.PROXY_METHOD_MAP.put(key, proxyMethodDto);
    }

    /**
     * 获取代理方法
     * 
     * @param key
     * @return
     */
    public ProxyMethodDto getProxyMethod(String key) {
        return AsyncProxy.PROXY_METHOD_MAP.get(key);
    }

    /**
     * 获取异步方法唯一标识
     *
     * @param bean
     * @param method
     * @return
     */
    public String getAsyncMethodKey(Object bean, Method method) {
        if (method.toString().contains(bean.getClass().getName())) {
            // 异步执行注解在当前类方法上面
            return Md5Util.md5(method.toString());
        } else {
            // 异步执行注解在基类方法上面
            return Md5Util.md5(bean.getClass().getSimpleName() + "#" + method);
        }
    }
}
