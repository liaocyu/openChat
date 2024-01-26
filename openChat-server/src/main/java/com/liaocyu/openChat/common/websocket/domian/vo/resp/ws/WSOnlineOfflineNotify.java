package com.liaocyu.openChat.common.websocket.domian.vo.resp.ws;

import com.liaocyu.openChat.common.chat.domain.vo.resp.ChatMemberResp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2023/12/8 14:59
 * @description :
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WSOnlineOfflineNotify {
    private List<ChatMemberResp> changeList = new ArrayList<>();//新的上下线用户
    private Long onlineNum;//在线人数
}