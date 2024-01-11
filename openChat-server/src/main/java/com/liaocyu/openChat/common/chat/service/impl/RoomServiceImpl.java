package com.liaocyu.openChat.common.chat.service.impl;

import com.liaocyu.openChat.common.common.domain.enums.NormalOrNoEnum;
import com.liaocyu.openChat.common.common.utils.AssertUtil;
import com.liaocyu.openChat.common.chat.dao.RoomDao;
import com.liaocyu.openChat.common.chat.dao.RoomFriendDao;
import com.liaocyu.openChat.common.chat.domain.entity.Room;
import com.liaocyu.openChat.common.chat.domain.entity.RoomFriend;
import com.liaocyu.openChat.common.chat.domain.enums.RoomTypeEnum;
import com.liaocyu.openChat.common.chat.service.RoomService;
import com.liaocyu.openChat.common.user.service.adapter.ChatAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/4 10:22
 * @description :
 */
@Service("roomService")
public class RoomServiceImpl implements RoomService {

    private final RoomDao roomDao;
    private final RoomFriendDao roomFriendDao;

    @Autowired
    public RoomServiceImpl(RoomDao roomDao , RoomFriendDao roomFriendDao) {
        this.roomDao = roomDao;
        this.roomFriendDao = roomFriendDao;
    }

    @Override
    public void disableFriendRoom(List<Long> uidList) {
        AssertUtil.isNotEmpty(uidList , "房间创建失败，好友数量不对");
        AssertUtil.equal(uidList.size() , 2 , "房间创建失败，好友数量不对");
        String key = ChatAdapter.generateRoomKey(uidList);
        roomFriendDao.disableRoom(key);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RoomFriend createFriendRoom(List<Long> uidList) {
        AssertUtil.isNotEmpty(uidList , "房间创建失败，好友数量不对");
        AssertUtil.equal(uidList.size() , 2 , "房间创建失败，好友数量不对");
        String key = ChatAdapter.generateRoomKey(uidList);

        RoomFriend roomFriend = roomFriendDao.getByKey(key);
        if(Objects.nonNull(roomFriend)) {
            //如果存在房间就恢复，适用于恢复好友场景
            restoreRoomIfNeed(roomFriend);
        } else {
            // 新建房间
            Room room = createRoom(RoomTypeEnum.FRIEND);
            roomFriend = createFriendRoom(room.getId(), uidList);
        }
        return null;
    }

    @Override
    public RoomFriend getFriendRoom(Long uid1, Long uid2) {
        // 10443  10003
        String key = ChatAdapter.generateRoomKey(Arrays.asList(uid1, uid2));

        return roomFriendDao.getByKey(key);
    }

    private RoomFriend createFriendRoom(Long roomId, List<Long> uidList) {
        // 创建好友房间
        RoomFriend insert = ChatAdapter.buildFriendRoom(roomId, uidList);
        roomFriendDao.save(insert);
        return insert;
    }

    private Room createRoom(RoomTypeEnum typeEnum) {
        Room insert = ChatAdapter.buildRoom(typeEnum);
        roomDao.save(insert);
        return insert;
    }

    private void restoreRoomIfNeed(RoomFriend room) {
        if (Objects.equals(room.getStatus() , NormalOrNoEnum.NOT_NORMAL.getStatus())) {
            roomFriendDao.restoreRoom(room.getId());
        }
    }
}
