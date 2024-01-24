package com.liaocyu.openChat.common.user.service.impl;

import com.liaocyu.openChat.common.common.annotation.RedissonLock;
import com.liaocyu.openChat.common.common.domain.enums.YesOrNoEnum;
import com.liaocyu.openChat.common.common.service.LockService;
import com.liaocyu.openChat.common.user.dao.UserBackpackDao;
import com.liaocyu.openChat.common.user.domain.entity.UserBackpack;
import com.liaocyu.openChat.common.user.domain.enums.IdempotentEnum;
import com.liaocyu.openChat.common.user.service.UserBackpackService;
import org.redisson.api.RedissonClient;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2023/12/25 10:58
 * @description :
 */
@Service("userBackpackService")
public class UserBackpackServiceImpl implements UserBackpackService {

    private final RedissonClient redissonClient;
    private final UserBackpackDao userBackpackDao;
    private final LockService lockService;

    @Autowired
    public UserBackpackServiceImpl(RedissonClient redissonClient
            , UserBackpackDao userBackpackDao
            ,LockService lockService
    ) {
        this.redissonClient = redissonClient;
        this.userBackpackDao = userBackpackDao;
        this.lockService = lockService;
    }

    /**
     * 使用最原始的分布式锁来实现
     * @param uid 用户Id
     * @param itemId 物品Id
     * @param idempotentEnum 幂等类型
     * @param businessId 幂等唯一标识
     */
    /*@Override
    public void acquireItem(Long uid, Long itemId, IdempotentEnum idempotentEnum, String businessId) {
        // 获取设置的幂等号
        String idempotent = getIdempotent(itemId, idempotentEnum, businessId);
        // 根据其获取的幂等号 设置一个分布式锁
        RLock lock = redissonClient.getLock("acquireItem" + idempotent);
        boolean b = lock.tryLock(); // 获取分布式锁立即失败
        // lock.lock(1000 , TimeUnit.MILLISECONDS); // 获取分布式锁等待指定时间 1s即刻失败
        AssertUtil.isTrue(b , "请求太频繁了");
        // 幂等相关的逻辑

        try {
            // 判断幂等键是否存在
            UserBackpack userBackpack = userBackpackDao.getByIdempotent(idempotent);
            if(Objects.nonNull(userBackpack)) {
                return;
            }
            // 发放物品；
            UserBackpack userBackpack1 = UserBackpack.builder()
                    .uid(uid)
                    .itemId(itemId)
                    .status(YesOrNoEnum.NO.getStatus())
                    .idempotent(idempotent)
                    .build();
            userBackpackDao.save(userBackpack1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }*/

    /**
     * 使用编程式实现分布式锁
     * @param uid 用户Id
     * @param itemId 物品Id
     * @param idempotentEnum 幂等类型
     * @param businessId 幂等唯一标识
     */
    /*@Override
    public void acquireItem(Long uid, Long itemId, IdempotentEnum idempotentEnum, String businessId) {
        // 获取设置的幂等号
        String idempotent = getIdempotent(itemId, idempotentEnum, businessId);
        lockService.executeWithLock("acquireItem" + idempotent , () -> {
            // 判断幂等键是否存在
            UserBackpack userBackpack = userBackpackDao.getByIdempotent(idempotent);
            if (Objects.nonNull(userBackpack)) {
                return;
            }
            // 发放物品；
            UserBackpack userBackpack1 = UserBackpack.builder()
                    .uid(uid)
                    .itemId(itemId)
                    .status(YesOrNoEnum.NO.getStatus())
                    .idempotent(idempotent)
                    .build();
            userBackpackDao.save(userBackpack1);
        });

    }*/

    /**
     * 使用注解是实现分布式锁
     * @param uid 用户Id
     * @param itemId 物品Id
     * @param idempotentEnum 幂等类型
     * @param businessId 幂等唯一标识
     */
    @Override
    public void acquireItem(Long uid , Long itemId , IdempotentEnum idempotentEnum , String businessId) {
        String idempotent = getIdempotent(itemId, idempotentEnum, businessId);
        /* 这里是同类调用 ， 在@Transactional标注的方法中，调用会失效的
        *  这里的解决方法是自己调用自己 ，并使用 @Lazy 注解来解决循环依赖的问题
        *       @Autowired
        *       @Lazy
        *       UserBackpackService userBackpackService
        *
        *  或者说 使用 AopContext.currentProxy() 来进行调用
        *       ((UserBackpackServiceImpl) AopContext.currentProxy()).doAcquireItem(uid , itemId , idempotent);
        * */
         // 使用当前代理去调用这个方法
        ((UserBackpackServiceImpl) AopContext.currentProxy()).doAcquireItem(uid , itemId , idempotent);
    }

    @Transactional
    @RedissonLock(key = "#idempotent" , waitTime = 5000) // 等待 5 秒进行排队
    public void doAcquireItem(Long uid, Long itemId, String idempotent) {
        UserBackpack userBackpack = userBackpackDao.getByIdempotent(idempotent);
        if(Objects.nonNull(userBackpack)) {
            return;
        }
        // 发放物品
        UserBackpack insert = UserBackpack.builder()
                .uid(uid)
                .itemId(itemId)
                .status(YesOrNoEnum.NO.getStatus())
                .idempotent(idempotent)
                .build();
        userBackpackDao.save(insert);
    }

    private String getIdempotent(Long itemId, IdempotentEnum idempotentEnum, String businessId) {
        return String.format("%d_%d_%s" , itemId , idempotentEnum.getType() , businessId );
    }
}
