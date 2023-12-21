package com.liaocyu.openChat.common.user.service.adapter;

import cn.hutool.core.bean.BeanUtil;
import com.liaocyu.openChat.common.user.domain.entity.User;
import com.liaocyu.openChat.common.user.domain.vo.resp.UserInfoResp;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2023/12/12 12:55
 * @description :
 */
public class UserAdapter {
    public static User buildUserSave(String openId) {
        return User.builder().openId(openId).build();
    }

    public static User buildAuthorizeUser(Long id, WxOAuth2UserInfo userInfo) {
        User user = new User();
        user.setId(id);
        user.setName(userInfo.getNickname());
        user.setAvatar(userInfo.getHeadImgUrl());
        user.setSex(userInfo.getSex());
        user.setIpInfo(user.getIpInfo());
        return user;
    }

    public static UserInfoResp buildUserInfo(User user, Integer modifyNameCount) {
        UserInfoResp vo = new UserInfoResp();
        BeanUtil.copyProperties(user , vo);
        vo.setModifyNameChance(modifyNameCount);
        return vo;
    }
}
