package com.liaocyu.openChat.common.chat.service.helper;

import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.StrUtil;
import com.liaocyu.openChat.common.websocket.domian.enums.ChatActiveStatusEnum;


/**
 * Description: 成员列表工具类
 * Author: <a href="https://github.com/liaocyu">liaocyu</a>
 * Date: 2023-03-28
 */
public class ChatMemberHelper {
    private static final String SEPARATOR = "_";

    public static Pair<ChatActiveStatusEnum, String> getCursorPair(String cursor) {
        ChatActiveStatusEnum activeStatusEnum = ChatActiveStatusEnum.ONLINE;
        String timeCursor = null;
        if (StrUtil.isNotBlank(cursor)) { // 2_1698332821589
            String activeStr = cursor.split(SEPARATOR)[0]; // 成员状态枚举值 1-在线 2-离线
            String timeStr = cursor.split(SEPARATOR)[1]; // 时间戳
            activeStatusEnum = ChatActiveStatusEnum.of(Integer.parseInt(activeStr)); // 根据status状态码返回相应的枚举值
            timeCursor = timeStr;
        }
        return Pair.of(activeStatusEnum, timeCursor); // 成员状态枚举值未作键  时间戳为值
    }

    /**
     *
     * @param activeStatusEnum 成员在线状态
     * @param timeCursor 时间戳
     * @return 2_1698332821589
     */
    public static String generateCursor(ChatActiveStatusEnum activeStatusEnum, String timeCursor) {
        return activeStatusEnum.getStatus() + SEPARATOR + timeCursor;
    }
}
