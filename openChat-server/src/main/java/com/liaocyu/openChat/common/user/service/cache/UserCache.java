package com.liaocyu.openChat.common.user.service.cache;

import com.liaocyu.openChat.common.common.constant.RedisKey;
import com.liaocyu.openChat.common.common.utils.RedisUtils;
import com.liaocyu.openChat.common.user.dao.BlackDao;
import com.liaocyu.openChat.common.user.dao.UserRoleDao;
import com.liaocyu.openChat.common.user.domain.entity.Black;
import com.liaocyu.openChat.common.user.domain.entity.UserRole;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private final BlackDao blackDao;

    @Autowired
    public UserCache(UserRoleDao userRoleDao , BlackDao blackDao
    ) {
        this.userRoleDao = userRoleDao;
        this.blackDao = blackDao;
    }

    /**
     * 获取指定用户的权限列表
     *
     * @param uid 指定用户
     */
    @Cacheable(cacheNames = "user", key = " 'role:'+#uid")
    public Set<Long> getRoleSet(Long uid) {
        List<UserRole> userRoles = userRoleDao.listByUid(uid);
        return userRoles.stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toSet());
    }

    /**
     * 获取用户拉黑列表
     * 最后将结果保存到缓存中去
     */
    @Cacheable(cacheNames = "user" , key = "'blacklist'")
    public Map<Integer, Set<String>> getBlackMap() {
        Map<Integer, List<Black>> collect = blackDao.list().stream().collect(Collectors.groupingBy(Black::getType));
        Map<Integer, Set<String>> result = new HashMap<>();
        collect.forEach((type , list) -> {
            result.put(type , list.stream().map(Black::getTarget).collect(Collectors.toSet()));
        });
        return result;
    }

    @CacheEvict(cacheNames = "user" , key = "'blackList'")
    public Map<Integer , Set<String>> evictBlackMap() {
        return null;
    }

    /**
     * 获取信息最后一次修改事件
     * @param uidList
     * @return
     */
    public List<Long> getUserModifyTime(List<Long> uidList) {
        List<String> keys = uidList.stream().map(uid -> RedisKey.getKey(RedisKey.USER_MODIFY_STRING , uid)).collect(Collectors.toList());
        return RedisUtils.mget(keys , Long.class);
    }
}
