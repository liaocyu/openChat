package com.liaocyu.openChat.common.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/10 10:21
 * @description :
 */
@AllArgsConstructor
@Getter
public enum BusinessErrorEnum implements ErrorEnum{
    //==================================common==================================
    BUSINESS_ERROR(1001, "{0}"),
    //==================================user==================================
    //==================================chat==================================
    SYSTEM_ERROR(1001, "系统出小差了，请稍后再试哦~~"),
    ;

    private Integer code;
    private String msg;
    @Override
    public Integer getErrorCode() {
        return null;
    }

    @Override
    public String getErrorMsg() {
        return null;
    }
}
