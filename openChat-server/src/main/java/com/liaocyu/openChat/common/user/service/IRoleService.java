package com.liaocyu.openChat.common.user.service;

import com.liaocyu.openChat.common.user.domain.entity.Role;
import com.baomidou.mybatisplus.extension.service.IService;
import com.liaocyu.openChat.common.user.domain.enums.RoleEnum;

/**
 * <p>
 * 角色表 服务类
 * </p>
 *
 * @author <a href="https://github.com/liaocyu">liaocyu</a>
 * @since 2023-12-28
 */
public interface IRoleService {

    /**
     * 是否拥有某个权限
     */
    boolean hasPower(Long uid , RoleEnum roleEnum);
}
