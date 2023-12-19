package com.liaocyu.openChat.common.common.thread;

import lombok.AllArgsConstructor;

import java.util.concurrent.ThreadFactory;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2023/12/13 14:55
 * @description :
 */
@AllArgsConstructor
public class MyThreadFactory implements ThreadFactory {

    private static final MyUncaughtExceptionHandler MY_UNCAUGHT_EXCEPTION_HANDLER = new MyUncaughtExceptionHandler();
    private ThreadFactory original;



    @Override
    public Thread newThread(Runnable r) {
        Thread thread = original.newThread(r);// 执行 spring 线程自己的创建逻辑
        thread.setUncaughtExceptionHandler(MY_UNCAUGHT_EXCEPTION_HANDLER); // 额外装饰我们需要的创建逻辑
        return thread;
    }


}
