package com.liaocyu.openChat.common.user.service;

import com.liaocyu.openChat.common.user.domain.entity.User;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author <a href="https://github.com/liaocyu">liaocyu</a>
 * @since 2023-12-08
 */
public interface UserService {

    /**
     * 用户注册
     *
     * @param insert 注册的用户
     * @return
     */
    Long register(User insert);
}
