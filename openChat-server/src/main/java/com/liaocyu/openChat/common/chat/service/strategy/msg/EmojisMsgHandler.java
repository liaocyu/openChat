package com.liaocyu.openChat.common.chat.service.strategy.msg;

import com.liaocyu.openChat.common.chat.dao.MessageDao;
import com.liaocyu.openChat.common.chat.domain.entity.Message;
import com.liaocyu.openChat.common.chat.domain.entity.msg.EmojisMsgDTO;
import com.liaocyu.openChat.common.chat.domain.entity.msg.MessageExtra;
import com.liaocyu.openChat.common.chat.domain.enums.MessageTypeEnum;
import com.liaocyu.openChat.common.chat.service.strategy.AbstractMsgHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/30 16:01
 * @description :
 */
@Component("emojisMsgHandler")
public class EmojisMsgHandler extends AbstractMsgHandler<EmojisMsgDTO> {

    @Autowired
    private MessageDao messageDao;


    @Override
    protected MessageTypeEnum getMsgTypeEnum() {
        return MessageTypeEnum.EMOJI;
    }

    @Override
    public void saveMsg(Message msg, EmojisMsgDTO body) {
        MessageExtra extra = Optional.ofNullable(msg.getExtra()).orElse(new MessageExtra());
        Message update = new Message();
        update.setId(msg.getId());
        update.setExtra(extra);
        extra.setEmojisMsgDTO(body);
        messageDao.updateById(update);
    }

    @Override
    public Object showMsg(Message msg) {
        return msg.getExtra().getEmojisMsgDTO();
    }

    @Override
    public Object showReplyMsg(Message msg) {
        return "表情";
    }

    @Override
    public String showContactMsg(Message msg) {
        return "[表情包]";
    }
}
