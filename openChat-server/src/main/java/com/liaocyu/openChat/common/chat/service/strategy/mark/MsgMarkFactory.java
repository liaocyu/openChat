package com.liaocyu.openChat.common.chat.service.strategy.mark;

import com.liaocyu.openChat.common.common.exception.CommonErrorEnum;
import com.liaocyu.openChat.common.common.utils.AssertUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/22 11:29
 * @description : 消息标记策略工厂
 */
public class MsgMarkFactory {
    private static final Map<Integer, AbstractMsgMarkStrategy> STRATEGY_MAP = new HashMap<>();

    public static void register(Integer markType, AbstractMsgMarkStrategy strategy) {
        STRATEGY_MAP.put(markType, strategy);
    }

    public static AbstractMsgMarkStrategy getStrategyNoNull(Integer markType) {
        AbstractMsgMarkStrategy strategy = STRATEGY_MAP.get(markType);
        AssertUtil.isNotEmpty(strategy, CommonErrorEnum.PARAM_VALID);
        return strategy;
    }
}

