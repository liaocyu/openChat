package com.liaocyu.openChat.common.user.service;

import com.liaocyu.openChat.common.chat.domain.vo.resp.IdRespVO;
import com.liaocyu.openChat.common.common.domain.vo.resp.ApiResult;
import com.liaocyu.openChat.common.user.domain.vo.req.user.UserEmojiReq;
import com.liaocyu.openChat.common.user.domain.vo.resp.UserEmojiResp;

import java.util.List;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/24 15:48
 * @description : 用户表情包
 */
public interface UserEmojiService {
    List<UserEmojiResp> list(Long uid);

    ApiResult<IdRespVO> insert(UserEmojiReq emojis, Long uid);

    void remove(Long id, Long uid);
}
