package com.liaocyu.openChat.common.common.event.listener;

import com.liaocyu.openChat.common.chat.dao.MessageDao;
import com.liaocyu.openChat.common.chat.domain.entity.Message;
import com.liaocyu.openChat.common.chat.domain.entity.Room;
import com.liaocyu.openChat.common.chat.domain.enums.HotFlagEnum;
import com.liaocyu.openChat.common.chat.service.WeChatMsgOperationService;
import com.liaocyu.openChat.common.chat.service.cache.RoomCache;
import com.liaocyu.openChat.common.chatai.service.IChatAIService;
import com.liaocyu.openChat.common.common.constant.MQConstant;
import com.liaocyu.openChat.common.common.domain.dto.MsgSendMessageDTO;
import com.liaocyu.openChat.common.common.event.MessageSendEvent;
import com.liaocyu.openchat.transaction.service.MQProducer;
import com.sun.xml.internal.bind.v2.TODO;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Objects;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/22 14:59
 * @description : 消息推送监听器
 */
@Component("messageSendListener")
@Slf4j
public class MessageSendListener {

    private final MessageDao messageDao;
    private final RoomCache roomCache;
    private final MQProducer mqProducer;
    private final WeChatMsgOperationService weChatMsgOperationService;
    private final IChatAIService openAIService;

    @Autowired
    public MessageSendListener(MessageDao messageDao , RoomCache roomCache , MQProducer mqProducer,
                               WeChatMsgOperationService weChatMsgOperationService , IChatAIService openAIService) {
        this.messageDao = messageDao;
        this.roomCache = roomCache;
        this.mqProducer = mqProducer;
        this.weChatMsgOperationService = weChatMsgOperationService;
        this.openAIService = openAIService;
    }

    // 消息推送方案设计
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT ,  classes = MessageSendEvent.class, fallbackExecution = true)
    public void messageRoute(MessageSendEvent event) {
        Long msgId = event.getMsgId();
        mqProducer.sendSecureMsg(MQConstant.SEND_MSG_TOPIC , new MsgSendMessageDTO(msgId) , msgId);
    }

    @TransactionalEventListener(classes = MessageSendEvent.class , fallbackExecution = true)
    public void handlerMsg(@NotNull MessageSendEvent event) {
        Message message = messageDao.getById(event.getMsgId());
        Room room = roomCache.get(message.getRoomId());
        if (isHotRoom(room)) {
            // OpenAI 服务
            openAIService.chat(message);
        }
    }

    public boolean isHotRoom(Room room) {
        return Objects.equals(HotFlagEnum.YES.getType(), room.getHotFlag());
    }

    /**
     * 给用户微信推送艾特好友的消息通知
     * （这个没开启，微信不让推）
     */
    @TransactionalEventListener(classes = MessageSendEvent.class, fallbackExecution = true)
    public void publishChatToWechat(@NotNull MessageSendEvent event) {
        Message message = messageDao.getById(event.getMsgId());
        if (Objects.nonNull(message.getExtra().getAtUidList())) {
            // 微信消息推送服务
             weChatMsgOperationService.publishChatMsgToWeChatUser(message.getFromUid(), message.getExtra().getAtUidList(),
                    message.getContent());
        }
    }

}
