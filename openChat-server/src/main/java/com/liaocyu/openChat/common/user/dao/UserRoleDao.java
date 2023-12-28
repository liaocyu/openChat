package com.liaocyu.openChat.common.user.dao;

import com.liaocyu.openChat.common.user.domain.entity.UserRole;
import com.liaocyu.openChat.common.user.mapper.UserRoleMapper;
import com.liaocyu.openChat.common.user.service.IUserRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户角色关系表 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/liaocyu">liaocyu</a>
 * @since 2023-12-28
 */
@Service
public class UserRoleDao extends ServiceImpl<UserRoleMapper, UserRole> {

}
