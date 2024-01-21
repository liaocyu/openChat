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
    BUSINESS_ERROR(0 , "{0}"),
    SYSTEM_ERROR(-1, "系统出小差了，请稍后再试"),
    PARAM_VALID(-2, "参数校验失败{0}"),
    FREQUENCY_LIMIT(-3, "请求太频繁了，请稍后再试"),
    LOCK_LIMIT(-4, "请求太频繁了，请稍后再试"),
    PARAM_INVALID(-2 , "参数校验失败{0}"),
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
