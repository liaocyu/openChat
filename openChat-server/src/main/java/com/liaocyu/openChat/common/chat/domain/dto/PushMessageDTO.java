package com.liaocyu.openChat.common.chat.domain.dto;

import com.liaocyu.openChat.common.chat.domain.enums.WSPushTypeEnum;
import com.liaocyu.openChat.common.websocket.domian.vo.resp.WSBaseResp;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/15 17:34
 * @description : 推送给用户的消息对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PushMessageDTO {

    /**
     * 推送的ws消息
     */
    private WSBaseResp<?> wsBaseMsg;
    /**
     * 推送的uid
     */
    private List<Long> uidList;

    /**
     * 推送类型 1个人 2全员
     *
     * @see
     */
    private Integer pushType;

    public PushMessageDTO(Long uid, WSBaseResp<?> wsBaseMsg) {
        this.uidList = Collections.singletonList(uid);
        this.wsBaseMsg = wsBaseMsg;
        this.pushType = WSPushTypeEnum.USER.getType();
    }

    public PushMessageDTO(List<Long> uidList, WSBaseResp<?> wsBaseMsg) {
        this.uidList = uidList;
        this.wsBaseMsg = wsBaseMsg;
        this.pushType = WSPushTypeEnum.USER.getType();
    }

    public PushMessageDTO(WSBaseResp<?> wsBaseMsg) {
        this.wsBaseMsg = wsBaseMsg;
        this.pushType = WSPushTypeEnum.ALL.getType();
    }
}
