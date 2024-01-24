package com.liaocyu.openChat.common.user.service.adapter;

import com.liaocyu.openChat.common.chat.domain.dto.ChatMessageMarkDTO;
import com.liaocyu.openChat.common.chat.domain.dto.ChatMsgRecallDTO;
import com.liaocyu.openChat.common.chat.service.ChatService;
import com.liaocyu.openChat.common.websocket.domian.enums.WSRespTypeEnum;
import com.liaocyu.openChat.common.websocket.domian.vo.resp.WSBaseResp;
import com.liaocyu.openChat.common.websocket.domian.vo.resp.ws.WSFriendApply;
import com.liaocyu.openChat.common.websocket.domian.vo.resp.ws.WSMsgMark;
import com.liaocyu.openChat.common.websocket.domian.vo.resp.ws.WSMsgRecall;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/22 11:47
 * @description : ws 消息适配器
 */
@Component
public class WSAdapter {
    private final ChatService chatService;

    @Autowired
    public WSAdapter(ChatService chatService) {
        this.chatService = chatService;
    }

    public static WSBaseResp<WSMsgMark> buildMsgMarkSend(ChatMessageMarkDTO dto, Integer markCount) {
        WSMsgMark.WSMsgMarkItem item = new WSMsgMark.WSMsgMarkItem();
        BeanUtils.copyProperties(dto, item);
        item.setMarkCount(markCount);
        WSBaseResp<WSMsgMark> wsBaseResp = new WSBaseResp<>();
        wsBaseResp.setType(WSRespTypeEnum.MARK.getType());
        WSMsgMark mark = new WSMsgMark();
        mark.setMarkList(Collections.singletonList(item));
        wsBaseResp.setData(mark);
        return wsBaseResp;
    }

    public static WSBaseResp<?> buildApplySend(WSFriendApply resp) {
        WSBaseResp<WSFriendApply> wsBaseResp = new WSBaseResp<>();
        wsBaseResp.setType(WSRespTypeEnum.APPLY.getType());
        wsBaseResp.setData(resp);
        return wsBaseResp;
    }

    public static WSBaseResp<?> buildMsgRecall(ChatMsgRecallDTO recallDTO) {
        WSBaseResp<WSMsgRecall> wsBaseResp = new WSBaseResp<>();
        wsBaseResp.setType(WSRespTypeEnum.RECALL.getType());
        WSMsgRecall recall = new WSMsgRecall();
        BeanUtils.copyProperties(recallDTO, recall);
        wsBaseResp.setData(recall);
        return wsBaseResp;
    }
}
