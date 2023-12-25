package com.liaocyu.openChat.common.common.service;

import com.liaocyu.openChat.common.common.exception.BusinessException;
import com.liaocyu.openChat.common.common.exception.CommonErrorEnum;
import lombok.SneakyThrows;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2023/12/25 11:49
 * @description :
 */
@Service
public class LockService {

    private final RedissonClient redissonClient;

    public LockService(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @SneakyThrows // Lombox 提供的注解 ，用于在方法上自动抛出已检查异常 checked exception ,无需在方法签名中声明或者捕获异常
    public <T> T executeWithLock(String key , int waitTime , TimeUnit timeUnit , Supplier<T> supplier){
        RLock lock = redissonClient.getLock(key);
        boolean success = lock.tryLock(waitTime, timeUnit);
        if (!success) {
            throw new BusinessException(CommonErrorEnum.LOCK_LIMIT);
        }

        try {
            return supplier.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }


    public <T> T executeWithLock(String key , Supplier<T> supplier){
        return executeWithLock(key , -1 , TimeUnit.MILLISECONDS , supplier);
    }

    public <T> T executeWithLock(String key , Runnable runnable){
        return executeWithLock(key , -1 , TimeUnit.MILLISECONDS , () -> {
            runnable.run();
            return null;
        });
    }
}
