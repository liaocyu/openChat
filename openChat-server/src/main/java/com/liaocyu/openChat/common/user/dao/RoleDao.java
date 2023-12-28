package com.liaocyu.openChat.common.user.dao;

import com.liaocyu.openChat.common.user.domain.entity.Role;
import com.liaocyu.openChat.common.user.mapper.RoleMapper;
import com.liaocyu.openChat.common.user.service.IRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 角色表 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/liaocyu">liaocyu</a>
 * @since 2023-12-28
 */
@Service
public class RoleDao extends ServiceImpl<RoleMapper, Role> {

}
