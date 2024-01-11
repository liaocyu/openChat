package com.liaocyu.openChat.common.user.service.adapter;

import com.liaocyu.openChat.common.common.domain.enums.NormalOrNoEnum;
import com.liaocyu.openChat.common.chat.domain.entity.Room;
import com.liaocyu.openChat.common.chat.domain.entity.RoomFriend;
import com.liaocyu.openChat.common.chat.domain.enums.HotFlagEnum;
import com.liaocyu.openChat.common.chat.domain.enums.RoomTypeEnum;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/7 14:39
 * @description :
 */
public class ChatAdapter {

    public static final String SEPARATOR = ",";

    public static String generateRoomKey(List<Long> uidList) {
        // 10443  10003
        return uidList.stream()
                .sorted() // 将自己的uid和好友的uid进行排序后使用 , 进行拼接
                .map(String::valueOf)
                .collect(Collectors.joining(SEPARATOR));
    }

    public static Room buildRoom(RoomTypeEnum typeEnum) {
        Room room = new Room();
        room.setType(typeEnum.getType());
        room.setHotFlag(HotFlagEnum.NOT.getType());
        return room;
    }

    /**
     *
     * @param roomId uid,targetUid
     * @param uidList uid1,uid2
     * @return
     */
    public static RoomFriend buildFriendRoom(Long roomId, List<Long> uidList) {
        List<Long> collect = uidList.stream().sorted().collect(Collectors.toList());
        RoomFriend roomFriend = new RoomFriend();
        roomFriend.setRoomId(roomId);
        // 更小的uid
        roomFriend.setUid1(collect.get(0));
        roomFriend.setUid2(collect.get(1));
        roomFriend.setRoomKey(generateRoomKey(uidList));
        roomFriend.setStatus(NormalOrNoEnum.NORMAL.getStatus());
        return roomFriend;
    }
}
