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
 * @createTime : 2024/1/12 15:42
 * @description : 群成员角色枚举
 * TODO 增加群成员角色在这里增加
 */
@AllArgsConstructor
@Getter
public enum GroupRoleAPPEnum {

    LEADER( 1 , "群主"),
    MANAGER( 2 , "管理"),
    MEMBER (3 , "普通成员"),
    REMOVE( 4 , "被剔除的成员"),
    ;

    private final Integer type;
    private final String desc;

    private static Map<Integer , GroupRoleAPPEnum> cache;

    static {
        cache = Arrays.stream(GroupRoleAPPEnum.values()).collect(Collectors.toMap(GroupRoleAPPEnum::getType, Function.identity()));
    }

    public static GroupRoleAPPEnum of(Integer type) {
        return cache.get(type);
    }


}
