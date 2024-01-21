package com.liaocyu.openChat.common.common.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/19 11:16
 * @description :
 */
@Getter
public class MessageSendEvent extends ApplicationEvent {
    private Long msgId;

    public MessageSendEvent(Object source, Long msgId) {
        super(source);
        this.msgId = msgId;
    }
}