package com.liaocyu.openChat.common.user.service.cache;

import cn.hutool.core.collection.CollUtil;
import com.liaocyu.openChat.common.common.constant.RedisKey;
import com.liaocyu.openChat.common.common.utils.RedisUtils;
import com.liaocyu.openChat.common.user.dao.BlackDao;
import com.liaocyu.openChat.common.user.dao.UserDao;
import com.liaocyu.openChat.common.user.dao.UserRoleDao;
import com.liaocyu.openChat.common.user.domain.entity.Black;
import com.liaocyu.openChat.common.user.domain.entity.User;
import com.liaocyu.openChat.common.user.domain.entity.UserRole;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2023/12/28 11:23
 * @description : 用户相关缓存
 */
@Component("userCache")
public class UserCache {

    private final UserRoleDao userRoleDao;
    private final BlackDao blackDao;
    private final UserDao userDao;

    @Autowired
    public UserCache(UserRoleDao userRoleDao , BlackDao blackDao , UserDao userDao
    ) {
        this.userRoleDao = userRoleDao;
        this.blackDao = blackDao;
        this.userDao = userDao;
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

    public Long getOnlineNum() {
        String onlineKey = RedisKey.getKey(RedisKey.ONLINE_UID_ZET);
        return RedisUtils.zCard(onlineKey);
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

    /**
     * 获取用户信息，盘路缓存模式
     */
    public User getUserInfo(Long uid) {//todo 后期做二级缓存
        return getUserInfoBatch(Collections.singleton(uid)).get(uid);
    }

    /**
     * 获取用户信息，盘路缓存模式
     */
    public Map<Long, User> getUserInfoBatch(Set<Long> uids) {
        //批量组装key
        List<String> keys = uids.stream().map(a -> RedisKey.getKey(RedisKey.USER_INFO_STRING, a)).collect(Collectors.toList());
        //批量get
        List<User> mget = RedisUtils.mget(keys, User.class);
        Map<Long, User> map = mget.stream().filter(Objects::nonNull).collect(Collectors.toMap(User::getId, Function.identity()));
        //发现差集——还需要load更新的uid
        List<Long> needLoadUidList = uids.stream().filter(a -> !map.containsKey(a)).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(needLoadUidList)) {
            //批量load
            List<User> needLoadUserList = userDao.listByIds(needLoadUidList);
            Map<String, User> redisMap = needLoadUserList.stream().collect(Collectors.toMap(a -> RedisKey.getKey(RedisKey.USER_INFO_STRING, a.getId()), Function.identity()));
            RedisUtils.mset(redisMap, 5 * 60);
            //加载回redis
            map.putAll(needLoadUserList.stream().collect(Collectors.toMap(User::getId, Function.identity())));
        }
        return map;
    }
}
