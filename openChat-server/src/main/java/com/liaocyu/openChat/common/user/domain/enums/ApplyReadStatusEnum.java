package com.liaocyu.openChat.common.user.domain.enums;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/8 10:34
 * @description :
 */
public enum ApplyReadStatusEnum {

    UNREAD(1, "未读"),
    READ(2, "已读"),
    ;

    ApplyReadStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private final Integer code;

    private final String desc;

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
