package com.liaocyu.openChat.common.common.constant;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2023/12/12 16:54
 * @description :
 */
public class RedisKey {
    public static final String BASE_KEY = "openchat:chat";

    /**
     * 用户token的key
     */
    public static final String USER_TOKEN_STRING = "userToken:uid_%d";

    // 拼接基础key
    public static String getKey(String key , Object... o) {
        return BASE_KEY + String.format(key , o);
    }
}
