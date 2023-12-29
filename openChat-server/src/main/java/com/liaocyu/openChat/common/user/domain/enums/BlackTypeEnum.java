package com.liaocyu.openChat.common.user.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Description: 拉黑类型枚举
 * Author: <a href="https://github.com/liaocyu">liaocyu</a>
 * Date: 2023-03-19
 * 根据UID或者根据IP进行拉黑用户
 */
@AllArgsConstructor
@Getter
public enum BlackTypeEnum {
    UID( 1, "UID"),
    IP(2 , "IP"),
    ;

    private final Integer type;
    private final String desc;

    private static Map<Integer, BlackTypeEnum> cache;

    static {
        cache = Arrays.stream(BlackTypeEnum.values()).collect(Collectors.toMap(BlackTypeEnum::getType, Function.identity()));
    }

    public static BlackTypeEnum of(Integer type) {
        return cache.get(type);
    }
}
