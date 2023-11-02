package com.xy.async.config;

import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * SpringBeanConfig
 *
 * @author xiongyan
 * @date 2021/4/2
 */
@Order(value = Ordered.HIGHEST_PRECEDENCE)
@Component
public class SpringBeanConfig implements ApplicationContextAware {

    public static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        SpringBeanConfig.applicationContext = applicationContext;
    }

    public static <T> T getBean(String name, Class<T> clazz) throws BeansException {
        return applicationContext.getBean(name, clazz);
    }

    public static <T> T getBean(Class<T> clazz) throws BeansException {
        return applicationContext.getBean(clazz);
    }

    public static Object getBean(String name) {
        return applicationContext.getBean(name);
    }

    public static  <T> Map<String, T> getBeansOfType(Class<T> clazz) throws BeansException {
        return applicationContext.getBeansOfType(clazz);
    }
}
