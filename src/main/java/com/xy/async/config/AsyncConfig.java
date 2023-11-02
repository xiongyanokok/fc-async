package com.xy.async.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.ttl.threadpool.TtlExecutors;

/**
 * AsyncConfig
 *
 * @author xiongyan
 * @date 2021/11/15
 */
@Configuration
@ComponentScan({ "com.xy.async" })
@ConditionalOnProperty(prefix = "async", value = "enabled", havingValue = "true")
@EnableConfigurationProperties(AsyncDataSourceConfig.class)
public class AsyncConfig {

    @Value("${async.executor.thread.corePoolSize:10}")
    private int corePoolSize;

    @Value("${async.executor.thread.maxPoolSize:50}")
    private int maxPoolSize;

    @Value("${async.executor.thread.queueCapacity:10000}")
    private int queueCapacity;

    @Value("${async.executor.thread.keepAliveSeconds:600}")
    private int keepAliveSeconds;

    @Bean("asyncJdbcTemplate")
    public JdbcTemplate jdbcTemplate(AsyncDataSourceConfig asyncDataSourceConfig) throws Exception {
        return new JdbcTemplate(DruidDataSourceFactory.createDataSource(asyncDataSourceConfig));
    }

    @Bean(name = "asyncExecute")
    public Executor asyncExecute() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 核心线程数
        executor.setCorePoolSize(corePoolSize);
        // 最大线程数
        executor.setMaxPoolSize(maxPoolSize);
        // 队列容量
        executor.setQueueCapacity(queueCapacity);
        // 活跃时间
        executor.setKeepAliveSeconds(keepAliveSeconds);
        // 线程名字前缀
        executor.setThreadNamePrefix("asyncExecute-");

        // setRejectedExecutionHandler：当pool已经达到max size的时候，如何处理新任务
        // CallerRunsPolicy：不在新线程中执行任务，而是由调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return TtlExecutors.getTtlExecutor(executor);
    }

}
