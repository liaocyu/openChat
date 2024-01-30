package com.liaocyu.openChat.common.chat.service.strategy.msg;

import com.liaocyu.openChat.common.chat.dao.MessageDao;
import com.liaocyu.openChat.common.chat.domain.entity.Message;
import com.liaocyu.openChat.common.chat.domain.enums.MessageTypeEnum;
import com.liaocyu.openChat.common.chat.service.strategy.AbstractMsgHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/30 16:10
 * @description : 系统消息
 */
@Component("systemMsgHandler")
public class SystemMsgHandler extends AbstractMsgHandler<String> {

    @Autowired
    private MessageDao messageDao;


    @Override
    protected MessageTypeEnum getMsgTypeEnum() {
        return MessageTypeEnum.SYSTEM;
    }

    @Override
    public void saveMsg(Message msg, String body) {
        Message update = new Message();
        update.setId(msg.getId());
        update.setContent(body);
        messageDao.updateById(update);
    }

    @Override
    public Object showMsg(Message msg) {
        return msg.getContent();
    }

    @Override
    public Object showReplyMsg(Message msg) {
        return msg.getContent();
    }

    @Override
    public String showContactMsg(Message msg) {
        return msg.getContent();
    }
}
