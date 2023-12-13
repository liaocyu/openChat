package com.liaocyu.openChat.common.user.service.impl;

import com.liaocyu.openChat.common.common.constant.RedisKey;
import com.liaocyu.openChat.common.common.utils.RedisUtils;
import com.liaocyu.openChat.common.user.service.LoginService;
import com.liaocyu.openChat.common.common.utils.JwtUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2023/12/12 14:37
 * @description :
 */
@Service
public class LoginServiceImpl implements LoginService {

    public static final int TOKEN_EXPIRE_DAYS = 3;
    public static final int TOKEN_RENEWAL_DAYS = 1;
    @Autowired
    JwtUtils jwtUtils;


    @Override
    public boolean verify(String token) {
        return false;
    }

    /**
     * 刷新token
     * @param token
     */
    @Override
    @Async
    public void renewalTokenIfNecessary(String token) {

        Long uid = getValidUid(token);
        String userTokenKey = getUserTokenKey(uid);
        Long expire = RedisUtils.getExpire(userTokenKey, TimeUnit.DAYS);
        if(expire == -2) {
            // 表示不存在的key
            return;
        }
        if(expire < TOKEN_RENEWAL_DAYS) { // 如果取出的过期时间小于一天
            RedisUtils.expire(getUserTokenKey(uid) , TOKEN_EXPIRE_DAYS , TimeUnit.DAYS);
        }
    }

    @Override
    public String login(Long id) { // TODO 返给前端TOEKN
        String token = jwtUtils.createToken(id);
        RedisUtils.set(getUserTokenKey(id), token, TOKEN_EXPIRE_DAYS, TimeUnit.DAYS);
        return token;
    }

    @Override
    public Long getValidUid(String token) {
        Long uid = jwtUtils.getUidOrNull(token);
        if(Objects.isNull(uid)) {
            return null;
        }
        String oldToken = RedisUtils.get(getUserTokenKey(uid));
        return Objects.equals(oldToken,token)?uid:null;
    }

    private String getUserTokenKey(Long uid) {
        return RedisKey.getKey(RedisKey.USER_TOKEN_STRING , uid);
    }
}
