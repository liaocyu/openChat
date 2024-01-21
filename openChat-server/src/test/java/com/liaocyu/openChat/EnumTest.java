package com.liaocyu.openChat;

import com.liaocyu.openChat.common.websocket.domian.enums.ChatActiveStatusEnum;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/18 10:26
 * @description :
 */
@SpringBootTest
public class EnumTest {
    @Test
    void ChatActiveStatusEnumTest() {

        ChatActiveStatusEnum activeStatusEnum = ChatActiveStatusEnum.ONLINE;

        System.out.println("activeStatusEnum " + activeStatusEnum); // ONLINE
        // 通过状态码获取枚举值
        ChatActiveStatusEnum onlineStatus = ChatActiveStatusEnum.of(1);
        System.out.println("Online status: " + onlineStatus); // Online status: ONLINE

        ChatActiveStatusEnum offlineStatus = ChatActiveStatusEnum.of(2);
        System.out.println("Offline status: " + offlineStatus); // Offline status: OFFLINE

        // 遍历所有枚举值
        for (ChatActiveStatusEnum status : ChatActiveStatusEnum.values()) {
            System.out.println("Status: " + status + ", Code: " + status.getStatus() + ", Desc: " + status.getDesc());
        }
        /*Status: ONLINE, Code: 1, Desc: 在线
        Status: OFFLINE, Code: 2, Desc: 离线*/
    }
}
