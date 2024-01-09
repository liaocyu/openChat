package com.liaocyu.openChat.common.common.service;

import com.liaocyu.openChat.common.common.exception.BusinessException;
import com.liaocyu.openChat.common.common.exception.CommonErrorEnum;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
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
@Slf4j
public class LockService {

    private final RedissonClient redissonClient;

    @Autowired
    public LockService(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    public <T> T executeWithLockThrows(String key , int waitTime, TimeUnit unit, SupplierThrow<T> supplier) throws Throwable {
        RLock lock = redissonClient.getLock(key);
        boolean lockSuccess = lock.tryLock(waitTime, unit);
        if (!lockSuccess) {
            throw new BusinessException(CommonErrorEnum.LOCK_LIMIT);
        }

        try {
            return supplier.get(); // 执行锁内的代码逻辑
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @SneakyThrows // Lombox 提供的注解 ，用于在方法上自动抛出已检查异常 checked exception ,无需在方法签名中声明或者捕获异常
    public <T> T executeWithLock(String key , int waitTime , TimeUnit timeUnit , Supplier<T> supplier){
        return executeWithLockThrows(key, waitTime, timeUnit, supplier::get);
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

    @FunctionalInterface
    public interface SupplierThrow<T> {

        /**
         * Gets a result.
         *
         * @return a result
         */
        T get() throws  Throwable;
    }
}
