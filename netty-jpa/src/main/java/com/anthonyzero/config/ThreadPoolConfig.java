package com.anthonyzero.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author admin
 */
@Data
@Configuration
@Component
@ConfigurationProperties(prefix = "thread.pool")
public class ThreadPoolConfig {

    private int coreSize;

    private int maxSize;

    private int aliveTime;

    private int queueCapacity;

    @Primary
    @Bean
    public AsyncTaskExecutor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(coreSize);
        executor.setMaxPoolSize(maxSize);
        executor.setKeepAliveSeconds(aliveTime);
        //LinkedBlockingQueue
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("business-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return executor;
    }
}
