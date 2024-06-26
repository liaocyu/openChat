package com.liaocyu.openChat.common.websocket.domian.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2023/12/8 15:05
 * @description : 成员状态枚举值
 */
@AllArgsConstructor
@Getter
public enum ChatActiveStatusEnum {
    ONLINE(1 , "在线"),
    OFFLINE(2 , "离线");

    private final Integer status;
    private final String desc;

    private static Map<Integer, ChatActiveStatusEnum> cache;

    static {
        /**
         * ChatActiveStatusEnum.values() 返回存储枚举值的数组
         * Collectors.toMap(ChatActiveStatusEnum::getStatus, Function.identity())
         * 将每个枚举值的状态作为键 ，Function.identity() 枚举值本身作为值（1 , "在线"）映射回相应的函数
         */
        cache = Arrays.stream(ChatActiveStatusEnum.values()).collect(
                Collectors.toMap(ChatActiveStatusEnum::getStatus, Function.identity()));
    }

    /**
     *
     * @param type 根据status 返回相应的枚举
     * @return
     */
    public static ChatActiveStatusEnum of(Integer type) {
        return cache.get(type);
    }
}
