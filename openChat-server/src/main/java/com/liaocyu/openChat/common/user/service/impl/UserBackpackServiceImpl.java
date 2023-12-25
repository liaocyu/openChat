package com.liaocyu.openChat.common.user.service.impl;

import com.liaocyu.openChat.common.common.domain.enums.YesOrNoEnum;
import com.liaocyu.openChat.common.common.service.LockService;
import com.liaocyu.openChat.common.common.utils.AssertUtil;
import com.liaocyu.openChat.common.user.dao.UserBackpackDao;
import com.liaocyu.openChat.common.user.domain.entity.UserBackpack;
import com.liaocyu.openChat.common.user.domain.enums.IdempotentEnum;
import com.liaocyu.openChat.common.user.service.IUserBackpackService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2023/12/25 10:58
 * @description :
 */
@Service
public class UserBackpackServiceImpl implements IUserBackpackService {

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
     * 最原始的获取物品逻辑
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
     * 升级改造后的物品逻辑
     * @param uid 用户Id
     * @param itemId 物品Id
     * @param idempotentEnum 幂等类型
     * @param businessId 幂等唯一标识
     */
    @Override
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

    }

    private String getIdempotent(Long itemId, IdempotentEnum idempotentEnum, String businessId) {
        return String.format("%d_%d_%s" , itemId , idempotentEnum.getType() , businessId );
    }
}
