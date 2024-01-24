package com.liaocyu.openChat.common.chat.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/22 11:30
 * @description : 消息标记动作类型
 */
@AllArgsConstructor
@Getter
public enum MessageMarkActTypeEnum {
    MARK(1, "确认标记"),
    UN_MARK(2, "取消标记"),
    ;

    private final Integer type;
    private final String desc;

    private static Map<Integer, MessageMarkActTypeEnum> cache;

    static {
        cache = Arrays.stream(MessageMarkActTypeEnum.values()).collect(Collectors.toMap(MessageMarkActTypeEnum::getType, Function.identity()));
    }

    public static MessageMarkActTypeEnum of(Integer type) {
        return cache.get(type);
    }
}