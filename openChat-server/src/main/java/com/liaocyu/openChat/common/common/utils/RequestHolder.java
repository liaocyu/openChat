package com.liaocyu.openChat.common.common.utils;

import com.liaocyu.openChat.common.common.domain.dto.RequestInfo;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2023/12/19 14:15
 * @description :
 * 请求上下文
 */
public class RequestHolder {

    public static final ThreadLocal<RequestInfo> thread_local = new ThreadLocal<>();

    public static void set(RequestInfo requestInfo) {
        thread_local.set(requestInfo);
    }

    public static RequestInfo get() {
        return thread_local.get();
    }

    public static void remove() {
        thread_local.remove();
    }
}
