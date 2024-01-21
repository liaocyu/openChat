package com.liaocyu.openChat.common.chat.controller;

import com.liaocyu.openChat.common.chat.domain.vo.req.ChatMessageMarkReq;
import com.liaocyu.openChat.common.chat.domain.vo.req.ChatMessagePageReq;
import com.liaocyu.openChat.common.chat.domain.vo.req.ChatMessageReq;
import com.liaocyu.openChat.common.chat.domain.vo.resp.ChatMessageResp;
import com.liaocyu.openChat.common.chat.service.ChatService;
import com.liaocyu.openChat.common.common.domain.vo.resp.ApiResult;
import com.liaocyu.openChat.common.common.domain.vo.resp.CursorPageBaseResp;
import com.liaocyu.openChat.common.common.utils.RequestHolder;
import com.liaocyu.openChat.common.user.domain.enums.BlackTypeEnum;
import com.liaocyu.openChat.common.user.service.cache.UserCache;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Set;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/10 10:07
 * @description : 消息发送相关接口
 */
@RestController
@RequestMapping("/capi/chat")
@Api(tags = "消息发送相关接口")
@Slf4j
public class ChatController {

    private final ChatService chatService;
    private final UserCache userCache;

    @Autowired
    public ChatController(ChatService chatService , UserCache userCache) {
        this.chatService = chatService;
        this.userCache = userCache;
    }

    private Set<String> getBlackUidSet() {
        return userCache.getBlackMap().getOrDefault(BlackTypeEnum.UID.getType() , new HashSet<>());
    }

    @GetMapping("public/msg/page")
    @ApiOperation("消息列表")
//    @FrequencyControl(time = 120, count = 20, target = FrequencyControl.Target.IP)
    public ApiResult<CursorPageBaseResp<ChatMessageResp>> getMsgPage(@Valid ChatMessagePageReq request) {
        CursorPageBaseResp<ChatMessageResp> msgPage = chatService.getMsgPage(request, RequestHolder.get().getUid());
        filterBlackMsg(msgPage);
        return ApiResult.success(msgPage);
    }

    private void filterBlackMsg(CursorPageBaseResp<ChatMessageResp> memberPage) {
        Set<String> blackMembers = getBlackUidSet();
        memberPage.getList().removeIf(a -> blackMembers.contains(a.getFromUser().getUid().toString()));
    }


    /**
     * 发送消息
     * @param request 消息请求体
     *        {
     *              "roomId": 0//会话id
     *              "msgType": 0,//消息类型 1文本消息 2撤回消息 3.图片消息 4.文件 5.语音 6视频
     *              "body": {},  //消息内容 根据类型不同请求不同
     *        }
     * @return
     */
    @PostMapping("msg")
    @ApiOperation("发送消息")
    // TODO 频控注解
    /*@FrequencyControl(time = 5, count = 3, target = FrequencyControl.Target.UID)
    @FrequencyControl(time = 30, count = 5, target = FrequencyControl.Target.UID)
    @FrequencyControl(time = 60, count = 10, target = FrequencyControl.Target.UID)*/
    public ApiResult<ChatMessageResp> sendMsg(@Valid @RequestBody ChatMessageReq request) {
        Long msgId = chatService.sendMsg(request, RequestHolder.get().getUid());
        //返回完整消息格式，方便前端展示
        return ApiResult.success(chatService.getMsgResp(msgId, RequestHolder.get().getUid()));
    }

    @PutMapping("msg/mark")
    @ApiOperation("消息标记")
    // TODO 频控注解 @FrequencyControl(time = 10, count = 5, target = FrequencyControl.Target.UID)
    public ApiResult<Void> setMsgMark(@Valid @RequestBody ChatMessageMarkReq request) {
        chatService.setMsgMark(RequestHolder.get().getUid(), request);
        return ApiResult.success();
    }


}
