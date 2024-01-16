package com.liaocyu.openChat.common.chat.service.cache;

import com.liaocyu.openChat.common.chat.dao.GroupMemberDao;
import com.liaocyu.openChat.common.chat.dao.MessageDao;
import com.liaocyu.openChat.common.chat.dao.RoomGroupDao;
import com.liaocyu.openChat.common.chat.domain.entity.RoomGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/13 15:33
 * @description :
 */
@Component("groupMemberCache")
public class GroupMemberCache {

    @Autowired
    private MessageDao messageDao;
    @Autowired
    private RoomGroupDao roomGroupDao;
    @Autowired
    private GroupMemberDao groupMemberDao;

    @Cacheable(cacheNames = "member" , key = "'groupMember'+#roomId")
    public List<Long> getMemberUidList(Long roomId) {
        RoomGroup roomGroup = roomGroupDao.getByRoomId(roomId);
        if(Objects.isNull(roomGroup)) {
            return null;
        }
        return groupMemberDao.getMemberUidList(roomGroup.getId());
    }

    @CacheEvict(cacheNames = "member" , key = "'groupMember'+#roomId")
    public List<Long> evictMemberUidList(Long roomId) {
        return null;
    }
}
