package com.xy.async.config;

import java.lang.reflect.Method;

import com.xy.async.dto.ProxyMethodDto;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import com.xy.async.annotation.AsyncExec;

import cn.hutool.core.util.ArrayUtil;

/**
 * 异步执行初始化
 *
 * @author xiongyan
 * @date 2021/6/19
 */
@Component
@Order(value = -1)
@ConditionalOnProperty(prefix = "async", value = "enabled", havingValue = "true")
public class AsyncInitBean implements BeanPostProcessor {

    @Autowired
    private AsyncProxy asyncProxy;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Method[] methods = ReflectionUtils.getAllDeclaredMethods(bean.getClass());
        if (ArrayUtil.isEmpty(methods)) {
            return bean;
        }
        for (Method method : methods) {
            AsyncExec asyncExec = AnnotationUtils.findAnnotation(method, AsyncExec.class);
            if (null == asyncExec) {
                continue;
            }
            ProxyMethodDto proxyMethodDto = new ProxyMethodDto();
            proxyMethodDto.setBean(SpringBeanConfig.getBean(beanName));
            proxyMethodDto.setMethod(method);
            // 生成方法唯一标识
            String key = asyncProxy.getAsyncMethodKey(bean, method);
            asyncProxy.setProxyMethod(key, proxyMethodDto);
        }
        return bean;
    }



}
