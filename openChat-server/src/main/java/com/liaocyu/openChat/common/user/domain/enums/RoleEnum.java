package com.liaocyu.openChat.common.user.domain.enums;

import com.liaocyu.openChat.common.user.domain.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Description: 权限枚举
 * Author: <a href="https://github.com/liaocyu">liaocyu</a>
 * Date: 2023-03-19
 */
@AllArgsConstructor
@Getter
public enum RoleEnum {
    ADMIN( 1L, "超级管理员"),
    CHAT_MANAGER(2L , "普通管理员"),
    ;

    private final Long id;
    private final String desc;

    private static Map<Long, RoleEnum> cache;

    static {
        cache = Arrays.stream(RoleEnum.values()).collect(Collectors.toMap(RoleEnum::getId, Function.identity()));
    }

    public static RoleEnum of(Long id) {
        return cache.get(id);
    }
}
