package com.liaocyu.openChat.common.user.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/6 20:54
 * @description :
 */
@Getter
@AllArgsConstructor
public enum ApplyTypeEnum {

    ADD_FRIEND( 1 , "加好友"),
    REJ_FRIEND( 2 , "拒绝好友"),
    ;

    private final Integer code;

    private final String desc;
}
