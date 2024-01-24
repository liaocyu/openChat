package com.liaocyu.openChat.common.user.controller;

import com.liaocyu.openChat.common.chat.domain.vo.resp.IdRespVO;
import com.liaocyu.openChat.common.common.domain.vo.req.IdReqVO;
import com.liaocyu.openChat.common.common.domain.vo.resp.ApiResult;
import com.liaocyu.openChat.common.common.utils.RequestHolder;
import com.liaocyu.openChat.common.user.domain.vo.req.user.UserEmojiReq;
import com.liaocyu.openChat.common.user.domain.vo.resp.UserEmojiResp;
import com.liaocyu.openChat.common.user.service.UserEmojiService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/24 15:47
 * @description :
 */
@RestController
@RequestMapping("/capi/user/emoji")
@Api(tags = "用户表情包管理相关接口")
@RequiredArgsConstructor
public class UserEmojiController {

    private final UserEmojiService userEmojiService;

    @GetMapping("list")
    @ApiOperation("表情包列表")
    public ApiResult<List<UserEmojiResp>> getEmojisPage() {
        return ApiResult.success(userEmojiService.list(RequestHolder.get().getUid()));
    }

    @PostMapping()
    @ApiOperation("新增表情包")
    public ApiResult<IdRespVO> insertEmojis(@Valid @RequestBody UserEmojiReq req) {
        return userEmojiService.insert(req, RequestHolder.get().getUid());
    }

    @DeleteMapping()
    @ApiOperation("删除表情包")
    public ApiResult<Void> deleteEmojis(@Valid @RequestBody IdReqVO reqVO) {
        userEmojiService.remove(reqVO.getId(), RequestHolder.get().getUid());
        return ApiResult.success();
    }

}
