package com.liaocyu.openChat.common.user.consumer;

import com.liaocyu.openChat.common.common.constant.MQConstant;
import com.liaocyu.openChat.common.common.domain.dto.ScanSuccessMessageDTO;
import com.liaocyu.openChat.common.websocket.service.WebSocketService;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Description: 将扫码成功的信息发送给对应的用户,等待授权
 * Author: <a href="https://github.com/liaocyu">liaocyu</a>
 * Date: 2023-08-12
 * TODO: 如果报错，删掉这个类
 */
@RocketMQMessageListener(consumerGroup = MQConstant.SCAN_MSG_GROUP, topic = MQConstant.SCAN_MSG_TOPIC, messageModel = MessageModel.BROADCASTING)
@Component
public class ScanSuccessConsumer implements RocketMQListener<ScanSuccessMessageDTO> {
    @Autowired
    private WebSocketService webSocketService;

    @Override
    public void onMessage(ScanSuccessMessageDTO scanSuccessMessageDTO) {
        webSocketService.scanSuccess(scanSuccessMessageDTO.getCode());
    }

}
