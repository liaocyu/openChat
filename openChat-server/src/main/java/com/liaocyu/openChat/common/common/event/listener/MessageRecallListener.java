package com.liaocyu.openChat.common.common.event.listener;

import com.liaocyu.openChat.common.chat.domain.dto.ChatMsgRecallDTO;
import com.liaocyu.openChat.common.chat.service.ChatService;
import com.liaocyu.openChat.common.chat.service.cache.MsgCache;
import com.liaocyu.openChat.common.chat.service.impl.PushService;
import com.liaocyu.openChat.common.common.event.MessageRecallEvent;
import com.liaocyu.openChat.common.user.service.adapter.WSAdapter;
import com.liaocyu.openChat.common.websocket.service.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/22 17:10
 * @description :
 */
@Slf4j
@Component("messageRecallListener")
public class MessageRecallListener {

    private final MsgCache msgCache;
    private final PushService pushService;

    @Autowired
    public MessageRecallListener(MsgCache msgCache , PushService pushService) {
        this.msgCache = msgCache;
        this.pushService = pushService;
    }

    @Async
    @TransactionalEventListener(classes = MessageRecallEvent.class, fallbackExecution = true)
    public void evictMsg(MessageRecallEvent event) {
        ChatMsgRecallDTO recallDTO = event.getRecallDTO();
        msgCache.evictMsg(recallDTO.getMsgId());
    }

    @Async
    @TransactionalEventListener(classes = MessageRecallEvent.class, fallbackExecution = true)
    public void sendToAll(MessageRecallEvent event) {
        pushService.sendPushMsg(WSAdapter.buildMsgRecall(event.getRecallDTO()));
    }
}
