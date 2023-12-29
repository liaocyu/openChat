package com.liaocyu.openChat.common.common.config;

import com.liaocyu.openChat.common.common.thread.MyThreadFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 自建项目线程池
 * 没有用Excutors快速创建。是因为Excutors创建的线程池用的无界队列，有oom的风险
 */
@Configuration
@EnableAsync
public class ThreadPoolConfig implements AsyncConfigurer {
    /**
     * 项目共用线程池
     */
    public static final String OPENCHAT_EXECUTOR = "openchatExecutor";
    /**
     * websocket通信线程池
     */
    public static final String WS_EXECUTOR = "websocketExecutor";

    @Override
    public Executor getAsyncExecutor() {
        return openchatExecutor();
       /* return mallchatExecutor();*/

    }

    @Bean(OPENCHAT_EXECUTOR)
    @Primary
    public ThreadPoolTaskExecutor openchatExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(200);
        executor.setThreadNamePrefix("openchat-executor-"); // 设置线程前缀 排查 cpu占用问题
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());// 满了调用线程执行，认为重要任务
        executor.setThreadFactory(new MyThreadFactory(executor)); // 指定线程池的线程工厂
        executor.initialize();
        return executor;
    }

    @Bean(WS_EXECUTOR)
    @Primary
    public ThreadPoolTaskExecutor webSocketExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setCorePoolSize(16);
        executor.setMaxPoolSize(16);
        executor.setQueueCapacity(1000);
        executor.setThreadNamePrefix("webSocket-executor-"); // 设置线程前缀 排查 cpu占用问题
        // 对于 WebSocket 的连接 如果真的推送不过去 就直接扔掉这个任务
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
        executor.setThreadFactory(new MyThreadFactory(executor)); // 指定线程池的线程工厂
        executor.initialize();
        return executor;
    }
}