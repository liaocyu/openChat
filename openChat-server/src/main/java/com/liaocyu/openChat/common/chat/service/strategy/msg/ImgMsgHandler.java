package com.liaocyu.openChat.common.chat.service.strategy.msg;

import com.liaocyu.openChat.common.chat.dao.MessageDao;
import com.liaocyu.openChat.common.chat.domain.entity.Message;
import com.liaocyu.openChat.common.chat.domain.entity.msg.ImgMsgDTO;
import com.liaocyu.openChat.common.chat.domain.entity.msg.MessageExtra;
import com.liaocyu.openChat.common.chat.domain.enums.MessageTypeEnum;
import com.liaocyu.openChat.common.chat.service.strategy.AbstractMsgHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/30 16:06
 * @description : 图片消息
 */
@Component("imgMsgHandler")
public class ImgMsgHandler extends AbstractMsgHandler<ImgMsgDTO> {
    @Autowired
    private MessageDao messageDao;


    @Override
    protected MessageTypeEnum getMsgTypeEnum() {
        return MessageTypeEnum.IMG;
    }

    @Override
    public void saveMsg(Message msg, ImgMsgDTO body) {
        MessageExtra extra = Optional.ofNullable(msg.getExtra()).orElse(new MessageExtra());
        Message update = new Message();
        update.setId(msg.getId());
        update.setExtra(extra);
        extra.setImgMsgDTO(body);
        messageDao.updateById(update);
    }

    @Override
    public Object showMsg(Message msg) {
        return msg.getExtra().getImgMsgDTO();
    }

    @Override
    public Object showReplyMsg(Message msg) {
        return "图片";
    }

    @Override
    public String showContactMsg(Message msg) {
        return "[图片]";
    }
}