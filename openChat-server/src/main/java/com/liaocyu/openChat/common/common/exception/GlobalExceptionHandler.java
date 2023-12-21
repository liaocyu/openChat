package com.liaocyu.openChat.common.common.exception;

import com.liaocyu.openChat.common.common.domain.vo.resp.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.metadata.ValidateUnwrappedValue;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2023/12/21 14:21
 * @description :
 * 全局异常捕获处理器
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ApiResult<?> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        StringBuilder errorMsg = new StringBuilder() ;
        e.getBindingResult().getFieldErrors().forEach(x -> errorMsg.append(x.getField()).append(x.getDefaultMessage()).append(","));
        String message = errorMsg.toString();
        // message.substring(message.length() - 1) 去掉最后一个 ",";
        return ApiResult.fail(CommonErrorEnum.PARAM_INVALID.getCode() , message.substring( 0  ,message.length() - 1));
    }

    /**
     * 捕获业务异常 进行异常处理
     * @param e
     * @return
     */
    @ExceptionHandler(value = BusinessException.class)
    public ApiResult<?> businessException(BusinessException e) {
        log.info("business exception! the reason is:{} " , e.getMessage());
        return ApiResult.fail( e.getErrorCode() , e.getErrorMsg() );
    }

    /**
     * 拦截所有其他的异常
     */
    @ExceptionHandler(value = Throwable.class)
    public ApiResult<?> throwable(Throwable e) {
        log.error("system exception! the reason is:{} " , e.getMessage() , e);
        return ApiResult.fail( CommonErrorEnum.SYSTEM_ERROR );
    }



}
