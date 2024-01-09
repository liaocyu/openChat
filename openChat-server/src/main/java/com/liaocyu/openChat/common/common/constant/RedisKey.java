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
     * 在线用户列表
     */
    public static final String ONLINE_UID_ZET = "online";

    /**
     * 离线用户列表
     */
    public static final String OFFLINE_UID_ZET = "offline";

    /**
     * 用户token的key
     */
    public static final String USER_TOKEN_STRING = "userToken:uid_%d";

    /**
     * 用户的信息更新时间
     */
    public static final String USER_MODIFY_STRING = "userModify:uid_%d";

    /**
     * 热门房间列表
     */
    public static final String HOT_ROOM_ZET = "hotRoom";

    /**
     * 用户信息
     */
    public static final String USER_INFO_STRING = "userInfo:uid_%d";

    // 拼接基础key
    public static String getKey(String key , Object... o) {
        return BASE_KEY + String.format(key , o);
    }
}
