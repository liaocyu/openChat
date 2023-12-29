package com.liaocyu.openChat.common.user.service.impl;

import com.liaocyu.openChat.common.user.domain.enums.RoleEnum;
import com.liaocyu.openChat.common.user.service.IRoleService;
import com.liaocyu.openChat.common.user.service.cache.UserCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2023/12/28 11:21
 * @description :
 */
@Service
public class RoleServiceImpl implements IRoleService {

    private final UserCache userCache;

    @Autowired
    public RoleServiceImpl(UserCache userCache) {
        this.userCache = userCache;
    }


    /**
     * 判断用户是否拥有某个权限
     *
     * @param uid 用户
     * @param roleEnum 权限枚举
     * @return
     */
    @Override
    public boolean hasPower(Long uid, RoleEnum roleEnum) {
        Set<Long> roleSet = userCache.getRoleSet(uid);
        return isAdmin(roleSet) || roleSet.contains((roleEnum.getId()));
    }

    private boolean isAdmin(Set<Long> roleSet) {
        return roleSet.contains(RoleEnum.ADMIN.getId());
    }

}
