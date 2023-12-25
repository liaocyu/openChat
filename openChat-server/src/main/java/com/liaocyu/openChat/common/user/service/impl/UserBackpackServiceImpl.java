package com.liaocyu.openChat.common.user.service.impl;

import com.liaocyu.openChat.common.common.domain.enums.YesOrNoEnum;
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

    @Autowired
    public UserBackpackServiceImpl(RedissonClient redissonClient
            , UserBackpackDao userBackpackDao

    ) {
        this.redissonClient = redissonClient;
        this.userBackpackDao = userBackpackDao;
    }


    @Override
    public void acquireItem(Long uid, Long itemId, IdempotentEnum idempotentEnum, String businessId) {
        // 获取设置的幂等号
        String idempotent = getIdempotent(itemId, idempotentEnum, businessId);
        // 根据其获取的幂等号 设置一个分布式锁
        RLock lock = redissonClient.getLock("acquireItem" + idempotent);
        boolean b = lock.tryLock();
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
    }

    private String getIdempotent(Long itemId, IdempotentEnum idempotentEnum, String businessId) {
        return String.format("%d_%d_%s" , itemId , idempotentEnum.getType() , businessId );
    }
}
