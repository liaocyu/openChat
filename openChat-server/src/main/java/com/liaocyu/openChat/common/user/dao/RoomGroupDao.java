package com.liaocyu.openChat.common.user.dao;

import com.liaocyu.openChat.common.user.domain.entity.RoomGroup;
import com.liaocyu.openChat.common.user.mapper.RoomGroupMapper;
import com.liaocyu.openChat.common.user.service.IRoomGroupService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 群聊房间表 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/liaocyu">liaocyu</a>
 * @since 2024-01-03
 */
@Service
public class RoomGroupDao extends ServiceImpl<RoomGroupMapper, RoomGroup> {

}
