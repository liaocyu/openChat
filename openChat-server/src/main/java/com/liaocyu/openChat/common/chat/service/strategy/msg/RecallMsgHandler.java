package com.liaocyu.openChat.common.chat.service.strategy.msg;

import com.liaocyu.openChat.common.chat.dao.MessageDao;
import com.liaocyu.openChat.common.chat.domain.dto.ChatMsgRecallDTO;
import com.liaocyu.openChat.common.chat.domain.entity.Message;
import com.liaocyu.openChat.common.chat.domain.entity.msg.MessageExtra;
import com.liaocyu.openChat.common.chat.domain.entity.msg.MsgRecall;
import com.liaocyu.openChat.common.chat.domain.enums.MessageTypeEnum;
import com.liaocyu.openChat.common.chat.service.cache.MsgCache;
import com.liaocyu.openChat.common.chat.service.strategy.AbstractMsgHandler;
import com.liaocyu.openChat.common.common.event.MessageRecallEvent;
import com.liaocyu.openChat.common.user.domain.entity.User;
import com.liaocyu.openChat.common.user.service.cache.UserCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/22 17:01
 * @description : 撤回文本消息
 */
@Component("recallMsgHandler")
public class RecallMsgHandler extends AbstractMsgHandler<Object> {

    private final MessageDao messageDao;
    private final UserCache userCache;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final MsgCache msgCache;

    @Autowired
    public RecallMsgHandler(MessageDao messageDao, UserCache userCache, MsgCache msgCache ,
                            ApplicationEventPublisher applicationEventPublisher) {
        this.messageDao = messageDao;
        this.userCache = userCache;
        this.applicationEventPublisher = applicationEventPublisher;
        this.msgCache = msgCache;
    }

    @Override
    protected MessageTypeEnum getMsgTypeEnum() {
        return MessageTypeEnum.RECALL;
    }

    @Override
    protected void saveMsg(Message message, Object body) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object showMsg(Message msg) {
        MsgRecall recall = msg.getExtra().getRecall();
        User userInfo = userCache.getUserInfo(recall.getRecallUid());
        if (!Objects.equals(recall.getRecallUid(), msg.getFromUid())) {
            return "管理员\"" + userInfo.getName() + "\"撤回了一条成员消息";
        }
        return "\"" + userInfo.getName() + "\"撤回了一条消息";
    }

    @Override
    public Object showReplyMsg(Message msg) {
        return "原消息已被撤回";
    }

    public void recall(Long recallUid, Message message) {//todo 消息覆盖问题用版本号解决✔
        MessageExtra extra = message.getExtra();
        extra.setRecall(new MsgRecall(recallUid, new Date()));
        Message update = new Message();
        update.setId(message.getId());
        update.setType(MessageTypeEnum.RECALL.getType());
        update.setExtra(extra);
        messageDao.updateById(update);
        applicationEventPublisher.publishEvent(new MessageRecallEvent(this, new ChatMsgRecallDTO(message.getId(), message.getRoomId(), recallUid)));

    }
    @Override
    public String showContactMsg(Message msg) {
        return "撤回了一条消息";
    }
}
