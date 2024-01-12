package com.liaocyu.openChat.common.chat.service.cache;

import com.liaocyu.openChat.common.chat.dao.RoomDao;
import com.liaocyu.openChat.common.chat.dao.RoomFriendDao;
import com.liaocyu.openChat.common.chat.domain.entity.Room;
import com.liaocyu.openChat.common.common.constant.RedisKey;
import com.liaocyu.openChat.common.common.service.cache.AbstractRedisStringCache;
import com.liaocyu.openChat.common.user.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.annotation.Commit;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/12 11:43
 * @description : 房间基本信息的缓存
 */
@Component
public class RoomCache extends AbstractRedisStringCache<Long, Room> {

    private final UserDao userDao;
    private final RoomDao roomDao;
    private final RoomFriendDao roomFriendDao;

    @Autowired
    public RoomCache(UserDao userDao , RoomDao roomDao , RoomFriendDao roomFriendDao) {
        this.userDao = userDao;
        this.roomDao = roomDao;
        this.roomFriendDao = roomFriendDao;
    }
    @Override
    protected String getKey(Long roomId) {
        // roomInfo:roomId_%d
        return RedisKey.getKey(RedisKey.ROOM_INFO_STRING, roomId);
    }

    @Override
    protected Long getExpireSeconds() {
        return 5 * 60L ; // 保存 3个小时
    }

    @Override
    protected Map<Long, Room> load(List<Long> roomIds) {
        List<Room> rooms = roomDao.listByIds(roomIds);
        return rooms.stream().collect(Collectors.toMap(Room::getId, Function.identity()));
    }
}
