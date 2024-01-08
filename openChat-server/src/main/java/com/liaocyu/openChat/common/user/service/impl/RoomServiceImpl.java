package com.liaocyu.openChat.common.user.service.impl;

import com.liaocyu.openChat.common.common.domain.enums.NormalOrNoEnum;
import com.liaocyu.openChat.common.common.utils.AssertUtil;
import com.liaocyu.openChat.common.user.dao.RoomDao;
import com.liaocyu.openChat.common.user.dao.RoomFriendDao;
import com.liaocyu.openChat.common.user.domain.entity.Room;
import com.liaocyu.openChat.common.user.domain.entity.RoomFriend;
import com.liaocyu.openChat.common.user.domain.enums.RoomTypeEnum;
import com.liaocyu.openChat.common.user.service.IRoomService;
import com.liaocyu.openChat.common.user.service.adapter.ChatAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/4 10:22
 * @description :
 */
@Service("IRoomService")
public class RoomServiceImpl implements IRoomService {

    private final RoomFriendDao roomFriendDao;
    private final RoomDao roomDao;

    @Autowired
    public RoomServiceImpl(RoomFriendDao roomFriendDao , RoomDao roomDao) {
        this.roomFriendDao = roomFriendDao;
        this.roomDao = roomDao;
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
