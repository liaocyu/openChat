package com.liaocyu.openChat.common.common.event;

import com.liaocyu.openChat.common.chat.domain.dto.ChatMsgRecallDTO;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/22 17:05
 * @description :
 */
@Getter
public class MessageRecallEvent extends ApplicationEvent {

    private final ChatMsgRecallDTO recallDTO;

    public MessageRecallEvent(Object source , ChatMsgRecallDTO recallDTO) {
        super(source);
        this.recallDTO = recallDTO;
    }
}
