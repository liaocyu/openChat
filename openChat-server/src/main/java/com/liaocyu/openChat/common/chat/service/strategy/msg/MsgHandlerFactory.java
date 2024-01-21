package com.liaocyu.openChat.common.chat.service.strategy.msg;

import com.liaocyu.openChat.common.chat.service.strategy.AbstractMsgHandler;
import com.liaocyu.openChat.common.common.exception.CommonErrorEnum;
import com.liaocyu.openChat.common.common.utils.AssertUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/19 15:02
 * @description : 消息处理类工厂
 */
public class MsgHandlerFactory {
    private static final Map<Integer, AbstractMsgHandler> STRATEGY_MAP = new HashMap<>();

    /**
     * 注册消息处理类
     *
     * @param code 状态码
     * @param strategy 具体类型的消息处理类
     */
    public static void register(Integer code, AbstractMsgHandler strategy) {
        STRATEGY_MAP.put(code, strategy);
    }

    /**
     * 获取消息处理类
     *
     * @param code 状态码
     * @return
     */
    public static AbstractMsgHandler getStrategyNoNull(Integer code) {
        AbstractMsgHandler strategy = STRATEGY_MAP.get(code);
        AssertUtil.isNotEmpty(strategy, CommonErrorEnum.PARAM_VALID);
        return strategy;
    }
}
