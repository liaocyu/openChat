package com.liaocyu.openChat.common.chat.service.strategy.mark;

import com.liaocyu.openChat.common.chat.domain.enums.MessageMarkTypeEnum;
import org.springframework.stereotype.Component;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/22 14:37
 * @description : 点踩标记策略类
 */
@Component("disLikeStrategy")
public class DisLikeStrategy extends AbstractMsgMarkStrategy {

    @Override
    protected MessageMarkTypeEnum getTypeEnum() {
        return MessageMarkTypeEnum.DISLIKE;
    }

    @Override
    public void doMark(Long uid, Long msgId) {
        super.doMark(uid, msgId);
        //同时取消点赞的动作
        MsgMarkFactory.getStrategyNoNull(MessageMarkTypeEnum.LIKE.getType()).unMark(uid, msgId);
    }

}
