package com.xy.async.strategy;

import java.util.List;
import java.util.Map;

import org.springframework.aop.support.AopUtils;

import com.xy.async.config.SpringBeanConfig;
import com.xy.async.strategy.context.StrategyContext;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 策略工厂类
 *
 * @author xiongyan
 * @date 2019/10/23
 */
@Slf4j
public class StrategyFactory {

    /**
     * 执行策略
     * 
     * @param type
     * @param clazz
     * @return
     */
    public static <O extends StrategyContext, T extends StrategyService<O>> T doStrategy(String type, Class<T> clazz) {
        if (StrUtil.isEmpty(type) || null == clazz) {
            return null;
        }

        Map<String, T> beanMap = SpringBeanConfig.getBeansOfType(clazz);
        if (MapUtil.isEmpty(beanMap)) {
            log.error("策略实现类不存在，type = {}，clazz = {}", type, clazz.getName());
            return null;
        }
        try {
            T defaultStrategy = null;
            for (Map.Entry<String, T> entry : beanMap.entrySet()) {
                // 默认策略
                if (null == defaultStrategy) {
                    Class<?> targetClass = AopUtils.getTargetClass(entry.getValue());
                    DefaultStrategy annotation = targetClass.getAnnotation(DefaultStrategy.class);
                    if (null != annotation) {
                        defaultStrategy = entry.getValue();
                    }
                }
                // 策略类型列表
                List<String> types = entry.getValue().listType();
                if (CollUtil.isNotEmpty(types) && types.contains(type)) {
                    return entry.getValue();
                }
            }
            if (null != defaultStrategy) {
                return defaultStrategy;
            }
            log.error("策略类型不存在，type = {}，clazz = {}", type, clazz.getName());
        } catch (Exception e) {
            log.error("获取策略实现类失败，type = {}，clazz = {}", type, clazz.getName(), e);
        }
        return null;
    }

}
