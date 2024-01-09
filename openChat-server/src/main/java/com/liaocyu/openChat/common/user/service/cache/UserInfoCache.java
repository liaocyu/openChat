package com.liaocyu.openChat.common.user.service.cache;

import com.liaocyu.openChat.common.common.constant.RedisKey;
import com.liaocyu.openChat.common.common.service.cache.AbstractRedisStringCache;
import com.liaocyu.openChat.common.user.dao.UserDao;
import com.liaocyu.openChat.common.user.domain.entity.User;
import net.bytebuddy.asm.Advice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/9 16:24
 * @description :
 */
@Component
public class UserInfoCache extends AbstractRedisStringCache<Long , User> {

    private final UserDao userDao;

    @Autowired
    public UserInfoCache(UserDao userDao) {
        this.userDao = userDao;
    }
    @Override
    protected String getKey(Long uid) {
        return RedisKey.getKey(RedisKey.USER_INFO_STRING, uid);
    }

    @Override
    protected Long getExpireSeconds() {
        return  5 * 60L;
    }

    @Override
    protected Map<Long, User> load(List<Long> uidList) {
        List<User> needLoadUserList = userDao.listByIds(uidList);
        return needLoadUserList.stream().collect(Collectors.toMap(User::getId, Function.identity()));
    }
}
