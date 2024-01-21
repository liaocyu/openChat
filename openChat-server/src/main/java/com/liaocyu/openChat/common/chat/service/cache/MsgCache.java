package com.liaocyu.openChat.common.chat.service.cache;

import com.liaocyu.openChat.common.chat.dao.MessageDao;
import com.liaocyu.openChat.common.chat.domain.entity.Message;
import com.liaocyu.openChat.common.user.dao.BlackDao;
import com.liaocyu.openChat.common.user.dao.RoleDao;
import com.liaocyu.openChat.common.user.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/19 15:45
 * @description :消息相关缓存
 */
@Component("msgCache")
public class MsgCache {

    @Autowired
    private MessageDao messageDao;

    @Cacheable(cacheNames = "msg", key = "'msg'+#msgId")
    public Message getMsg(Long msgId) {
        return messageDao.getById(msgId);
    }

    @CacheEvict(cacheNames = "msg", key = "'msg'+#msgId")
    public Message evictMsg(Long msgId) {
        return null;
    }
}

