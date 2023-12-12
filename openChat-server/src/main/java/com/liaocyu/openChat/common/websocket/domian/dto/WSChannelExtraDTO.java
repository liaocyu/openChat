package com.liaocyu.openChat.common.websocket.domian.dto;

import lombok.Data;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2023/12/11 13:44
 * @description : channel 关联的信息
 * 讲用户的 channel 为键，WSChannelExtraDTO 为值保存在 concurrentHashMap
 */
@Data
public class WSChannelExtraDTO {
    private Long uid; // 该用户的 Id ，也即 openId
}
