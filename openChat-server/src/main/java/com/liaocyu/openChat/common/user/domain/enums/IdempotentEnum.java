package com.liaocyu.openChat.common.user.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2023/12/25 10:54
 * @description :
 */
@AllArgsConstructor
@Getter
public enum IdempotentEnum {

    UID(1 , "uid"),
    MSG_ID(2 , "消息id"),
    ;
    private final Integer type;
    private final String desc;
}
