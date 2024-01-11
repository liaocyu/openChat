package com.liaocyu.openChat.common.chat.controller;

import com.liaocyu.openChat.common.chat.domain.vo.req.ContactFriendReq;
import com.liaocyu.openChat.common.chat.domain.vo.resp.ChatRoomResp;
import com.liaocyu.openChat.common.chat.service.RoomAppService;
import com.liaocyu.openChat.common.common.domain.vo.req.CursorPageBaseReq;
import com.liaocyu.openChat.common.common.domain.vo.resp.ApiResult;
import com.liaocyu.openChat.common.common.domain.vo.resp.CursorPageBaseResp;
import com.liaocyu.openChat.common.common.utils.RequestHolder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/10 10:08
 * @description : 会话相关接口
 */
@RestController
@RequestMapping("/capi/chat")
@Api(tags = "聊天室相关接口")
@Slf4j
public class ContactController {

    private final RoomAppService roomAppService;

    @Autowired
    public ContactController(RoomAppService roomAppService) {
        this.roomAppService = roomAppService;
    }

    @GetMapping("/public/contact/page")
    @ApiOperation("会话列表")
    public ApiResult<CursorPageBaseResp<ChatRoomResp>> getRoomPage(@Valid CursorPageBaseReq request) {
        Long uid = RequestHolder.get().getUid();
        return ApiResult.success(roomAppService.getContactPage(request, uid));
    }

    @GetMapping("public/contact/detail/friend")
    @ApiOperation("会话详情(联系人列表发消息用)")
    public ApiResult<ChatRoomResp> getContactDetailByFriend(@Valid ContactFriendReq request) {
        Long uid = RequestHolder.get().getUid();
        return ApiResult.success(roomAppService.getContactDetailByFriend(uid, request.getUid()));
    }
}
