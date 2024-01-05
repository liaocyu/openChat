package com.liaocyu.openChat.common.user.service;

import com.liaocyu.openChat.common.user.domain.entity.Room;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 房间表 服务类
 * </p>
 *
 * @author <a href="https://github.com/liaocyu">liaocyu</a>
 * @since 2024-01-03
 */
public interface IRoomService {

    void disableFriendRoom(List<Long> asList);
}
