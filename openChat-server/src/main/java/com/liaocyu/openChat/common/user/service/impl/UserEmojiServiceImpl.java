package com.liaocyu.openChat.common.user.service.impl;

import com.liaocyu.openChat.common.chat.domain.vo.resp.IdRespVO;
import com.liaocyu.openChat.common.common.annotation.RedissonLock;
import com.liaocyu.openChat.common.common.domain.vo.resp.ApiResult;
import com.liaocyu.openChat.common.common.utils.AssertUtil;
import com.liaocyu.openChat.common.user.dao.UserEmojiDao;
import com.liaocyu.openChat.common.user.domain.entity.UserEmoji;
import com.liaocyu.openChat.common.user.domain.vo.req.user.UserEmojiReq;
import com.liaocyu.openChat.common.user.domain.vo.resp.UserEmojiResp;
import com.liaocyu.openChat.common.user.service.UserEmojiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/24 15:49
 * @description :
 */
@Service("userEmojiService")
@Slf4j
@RequiredArgsConstructor
public class UserEmojiServiceImpl implements UserEmojiService {

    private final UserEmojiDao userEmojiDao;
    @Override
    public List<UserEmojiResp> list(Long uid) {
        return userEmojiDao.listByUid(uid).stream().map(a -> UserEmojiResp.builder()
                .id(a.getId())
                .expressionUrl(a.getExpressionUrl())
                .build()).collect(Collectors.toList());
    }

    @Override
    @RedissonLock(key = "#uid")
    public ApiResult<IdRespVO> insert(UserEmojiReq req, Long uid) {
        //校验表情数量是否超过30
        int count = userEmojiDao.countByUid(uid);
        AssertUtil.isFalse(count > 30, "最多只能添加30个表情哦~~");
        //校验表情是否存在
        Integer existsCount = userEmojiDao.lambdaQuery()
                .eq(UserEmoji::getExpressionUrl, req.getExpressionUrl())
                .eq(UserEmoji::getUid, uid)
                .count();
        AssertUtil.isFalse(existsCount > 0, "当前表情已存在哦~~");
        UserEmoji insert = UserEmoji.builder().uid(uid).expressionUrl(req.getExpressionUrl()).build();
        userEmojiDao.save(insert);
        return ApiResult.success(IdRespVO.id(insert.getId()));
    }

    @Override
    public void remove(Long id, Long uid) {
        UserEmoji userEmoji = userEmojiDao.getById(id);
        AssertUtil.isNotEmpty(userEmoji, "表情不能为空");
        AssertUtil.equal(userEmoji.getUid(), uid, "不能删除别人的表情");
        userEmojiDao.removeById(id);
    }
}
