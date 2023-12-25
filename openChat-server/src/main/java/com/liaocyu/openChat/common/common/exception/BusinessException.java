package com.liaocyu.openChat.common.common.exception;

import lombok.Data;
import lombok.Getter;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2023/12/21 14:55
 * @description :
 * 自定义异常
 */
@Data
public class BusinessException extends  RuntimeException{

    protected Integer errorCode;

    protected  String errorMsg;

    public BusinessException(String errorMsg) {
        super(errorMsg);
        this.errorCode = CommonErrorEnum.BUSINESS_ERROR.getErrorCode();
        this.errorMsg = errorMsg;
    }

    public BusinessException( Integer errorCode , String errorMsg) {
        super(errorMsg);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public BusinessException( ErrorEnum errorEnum ) {
        super(errorEnum.getErrorMsg());
        this.errorCode = errorEnum.getErrorCode();
        this.errorMsg = errorEnum.getErrorMsg();
    }
}
