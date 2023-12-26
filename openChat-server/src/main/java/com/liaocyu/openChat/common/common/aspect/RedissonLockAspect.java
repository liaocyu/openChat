package com.liaocyu.openChat.common.common.aspect;

import com.liaocyu.openChat.common.common.annotation.RedissonLock;
import com.liaocyu.openChat.common.common.service.LockService;
import com.liaocyu.openChat.common.common.utils.SpElUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2023/12/25 15:07
 * @description :
 * 1、加锁  2、开启事务  3、提交事务  4、释放锁
 * 一定要确保加锁比事务注解限制性，保证数据一致性、避免死锁和性能问题
 *      如果其他线程在事务没有提交之前访问共享资源，可能会产生脏读
 *      先开启事务再加锁，可能会导致事务在等待锁的过程中长时间占用数据库连接资源，而锁的获取也可能会受到数据库连接的限制
 */
@Component
@Aspect
@Order(0) // 确保比事务注解先执行，分布式锁在事务外
public class RedissonLockAspect {

    private final LockService lockService;

    public RedissonLockAspect(LockService lockService) {
        this.lockService = lockService;
    }

    @Around("@annotation(redissonLock)")
    public Object around(ProceedingJoinPoint joinPoint , RedissonLock redissonLock) throws  Throwable {

        Signature signature = joinPoint.getSignature();
        Method method = ((MethodSignature) signature).getMethod();
        String prefix = StringUtils.isBlank(redissonLock.prefixKey()) ? SpElUtils.getMethodKey(method) : redissonLock.prefixKey();
        String key = SpElUtils.parseSpEl(method , joinPoint.getArgs(), redissonLock.key());
        // return lockService.executeWithLock();
        return null;
    }
}
