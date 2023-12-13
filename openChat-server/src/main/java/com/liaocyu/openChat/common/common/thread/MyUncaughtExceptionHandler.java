package com.liaocyu.openChat.common.common.thread;

import lombok.extern.slf4j.Slf4j;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2023/12/13 14:41
 * @description :
 */
@Slf4j
public class MyUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        log.error("Exception in thread" , e);
    }
}
