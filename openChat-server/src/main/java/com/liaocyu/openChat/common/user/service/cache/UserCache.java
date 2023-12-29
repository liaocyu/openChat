package com.liaocyu.openChat.common.user.service.cache;

import com.liaocyu.openChat.common.user.dao.UserRoleDao;
import com.liaocyu.openChat.common.user.domain.entity.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2023/12/28 11:23
 * @description :
 */
@Component
public class UserCache {

    private final UserRoleDao userRoleDao;

    @Autowired
    public UserCache(UserRoleDao userRoleDao) {
        this.userRoleDao = userRoleDao;
    }

    /**
     * 获取指定用户的权限列表
     * @param uid 指定用户
     */
    @Cacheable(cacheNames = "user" , key = " 'role:'+#uid")
    public Set<Long> getRoleSet(Long uid) {
        List<UserRole> userRoles = userRoleDao.listByUid(uid);
        return userRoles.stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toSet());
    }
}
