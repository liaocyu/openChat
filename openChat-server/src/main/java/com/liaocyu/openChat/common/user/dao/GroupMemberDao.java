package com.liaocyu.openChat.common.user.dao;

import com.liaocyu.openChat.common.user.domain.entity.GroupMember;
import com.liaocyu.openChat.common.user.mapper.GroupMemberMapper;
import com.liaocyu.openChat.common.user.service.IGroupMemberService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 群成员表 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/liaocyu">liaocyu</a>
 * @since 2024-01-03
 */
@Service
public class GroupMemberDao extends ServiceImpl<GroupMemberMapper, GroupMember> {

}
