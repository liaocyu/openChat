package com.liaocyu.openChat.common.chat.consumer;

import com.liaocyu.openChat.common.common.constant.MQConstant;
import com.liaocyu.openChat.common.common.domain.dto.MsgSendMessageDTO;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/10 10:09
 * @description : 发送消息更新房间收信箱，并同步给房间成员信箱
 * SEND_MSG_GROUP: chat_send_msg_group
 * SEND_MSG_TOPIC: chat_send_msg
 */
@RocketMQMessageListener(consumerGroup = MQConstant.SEND_MSG_GROUP, topic = MQConstant.SEND_MSG_TOPIC)
@Component
public class MsgSendConsumer implements RocketMQListener<MsgSendMessageDTO> {
    @Override
    public void onMessage(MsgSendMessageDTO msgSendMessageDTO) {

    }
}
