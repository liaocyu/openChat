package com.liaocyu.openChat.common.user.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/6 23:12
 * @description :
 */
@Getter
@AllArgsConstructor
public enum ApplyStatusEnum {

    WAIT_APPROVAL( 1 , "待审批") ,

    AGREE( 2 , "同意"),
    ;

    private final Integer code;

    private final String desc;
}
