package com.liaocyu.openChat.common.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2023/12/21 14:27
 * @description :
 */
@AllArgsConstructor
@Getter
public enum CommonErrorEnum implements ErrorEnum{

    PARAM_INVALID(-2 , "参数校验失败"),
    SYSTEM_ERROR(-1 ,  "系统出小差了,请稍后再试~~"),
    ;
    private final Integer code ;
    private final String msg ;

    @Override
    public Integer getErrorCode() {
        return code;
    }

    @Override
    public String getErrorMsg() {
        return msg;
    }
}
