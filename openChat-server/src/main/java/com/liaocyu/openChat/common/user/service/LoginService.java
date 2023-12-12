package com.liaocyu.openChat.common.user.service;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2023/12/12 14:36
 * @description :
 */
public interface LoginService {
    /**
     * 用户登录获取token
     * @param id 用户uid
     * @return
     */
    String login(Long id);
}
