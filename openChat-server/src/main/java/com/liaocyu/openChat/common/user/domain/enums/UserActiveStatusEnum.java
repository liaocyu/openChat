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
public enum UserActiveStatusEnum {

    ONLINE(1 , "在线"),
    OFFLINE(2 , "离线"),
    ;
    private final Integer Status;
    private final String desc;
}
