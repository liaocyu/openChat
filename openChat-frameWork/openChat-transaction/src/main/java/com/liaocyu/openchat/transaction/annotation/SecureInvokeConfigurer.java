package com.liaocyu.openchat.transaction.annotation;

import com.sun.istack.internal.Nullable;

import java.util.concurrent.Executor;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/15 16:48
 * @description :
 */
public interface SecureInvokeConfigurer {

    /**
     * 返回一个线程池
     */
    @Nullable
    default Executor getSecureInvokeExecutor() {
        return null;
    }

}
