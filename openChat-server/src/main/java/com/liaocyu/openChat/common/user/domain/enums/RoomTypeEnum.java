package com.liaocyu.openChat.common.user.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/7 14:56
 * @description :
 */
@AllArgsConstructor
@Getter
public enum RoomTypeEnum {

    GROUP(1, "群聊"),
    FRIEND(2, "单聊"),
    ;

    private final Integer type;
    private final String desc;

    private static Map<Integer, RoomTypeEnum> cache;

    static {
        cache = Arrays.stream(RoomTypeEnum.values()).collect(Collectors.toMap(RoomTypeEnum::getType, Function.identity()));
    }

    public static RoomTypeEnum of(Integer type) {
        return cache.get(type);
    }

}
