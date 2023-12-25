package com.liaocyu.openChat.common.user.service;

import com.liaocyu.openChat.common.user.domain.enums.IdempotentEnum;

/**
 * <p>
 * 用户背包表 服务类
 * </p>
 *
 * @author <a href="https://github.com/liaocyu">liaocyu</a>
 * @since 2023-12-18
 */
public interface IUserBackpackService{

    /**
     * 用户获取物品
     * @param uid 用户Id
     * @param itemId 物品Id
     * @param idempotentEnum 幂等类型
     * @param businessId 幂等唯一标识
     */
    void acquireItem(Long uid , Long itemId , IdempotentEnum idempotentEnum , String businessId);
}
