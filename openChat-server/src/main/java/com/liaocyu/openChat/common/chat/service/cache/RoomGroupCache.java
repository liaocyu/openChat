package com.liaocyu.openChat.common.chat.service.cache;

import com.liaocyu.openChat.common.chat.dao.RoomGroupDao;
import com.liaocyu.openChat.common.chat.domain.entity.RoomGroup;
import com.liaocyu.openChat.common.common.constant.RedisKey;
import com.liaocyu.openChat.common.common.service.cache.AbstractRedisStringCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/12 16:34
 * @description :
 */
@Component("roomGroupCache")
public class RoomGroupCache extends AbstractRedisStringCache<Long, RoomGroup> {

    @Autowired
    private RoomGroupDao roomGroupDao;

    @Override
    protected String getKey(Long roomId) {
        return RedisKey.getKey(RedisKey.GROUP_INFO_STRING , roomId);
    }

    @Override
    protected Long getExpireSeconds() {
        return 5 * 60L;
    }

    @Override
    protected Map<Long, RoomGroup> load(List<Long> roomIds) {
        List<RoomGroup> roomGroupList = roomGroupDao.listByRoomIds(roomIds);
        return roomGroupList.stream().collect(Collectors.toMap(RoomGroup::getRoomId, Function.identity()));
    }
}
