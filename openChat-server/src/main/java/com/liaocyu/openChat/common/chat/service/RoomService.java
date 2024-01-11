package com.liaocyu.openChat.common.chat.service;

import com.liaocyu.openChat.common.chat.domain.entity.RoomFriend;

import java.util.List;

/**
 * <p>
 * 房间表 服务类
 * </p>
 *
 * @author <a href="https://github.com/liaocyu">liaocyu</a>
 * @since 2024-01-03
 */
public interface RoomService {

    /**
     * 禁用一个单聊房间
     * @param uidList
     */
    void disableFriendRoom(List<Long> uidList);

    /**
     * 创建一个单聊房间
     * @param uidList uidList
     * @return
     */
    RoomFriend createFriendRoom(List<Long> uidList);

    RoomFriend getFriendRoom(Long uid1 , Long uid2);
}
