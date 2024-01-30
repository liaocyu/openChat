package com.liaocyu.openChat.common.chat.service.strategy.msg;

import com.liaocyu.openChat.common.chat.dao.MessageDao;
import com.liaocyu.openChat.common.chat.domain.entity.Message;
import com.liaocyu.openChat.common.chat.domain.entity.msg.MessageExtra;
import com.liaocyu.openChat.common.chat.domain.entity.msg.VideoMsgDTO;
import com.liaocyu.openChat.common.chat.domain.enums.MessageTypeEnum;
import com.liaocyu.openChat.common.chat.service.strategy.AbstractMsgHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/30 16:12
 * @description : 视频消息
 */
@Component("videoMsgHandler")
public class VideoMsgHandler extends AbstractMsgHandler<VideoMsgDTO> {
    @Autowired
    private MessageDao messageDao;


    @Override
    protected MessageTypeEnum getMsgTypeEnum() {
        return MessageTypeEnum.VIDEO;
    }

    @Override
    public void saveMsg(Message msg, VideoMsgDTO body) {
        MessageExtra extra = Optional.ofNullable(msg.getExtra()).orElse(new MessageExtra());
        Message update = new Message();
        update.setId(msg.getId());
        update.setExtra(extra);
        extra.setVideoMsgDTO(body);
        messageDao.updateById(update);
    }

    @Override
    public Object showMsg(Message msg) {
        return msg.getExtra().getVideoMsgDTO();
    }

    @Override
    public Object showReplyMsg(Message msg) {
        return "视频";
    }

    @Override
    public String showContactMsg(Message msg) {
        return "[视频]";
    }
}