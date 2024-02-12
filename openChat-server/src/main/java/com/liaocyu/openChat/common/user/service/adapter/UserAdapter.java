package com.liaocyu.openChat.common.user.service.adapter;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import com.liaocyu.openChat.common.common.domain.enums.YesOrNoEnum;
import com.liaocyu.openChat.common.user.domain.entity.ItemConfig;
import com.liaocyu.openChat.common.user.domain.entity.User;
import com.liaocyu.openChat.common.user.domain.entity.UserBackpack;
import com.liaocyu.openChat.common.user.domain.vo.resp.BadgeResp;
import com.liaocyu.openChat.common.user.domain.vo.resp.UserInfoResp;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2023/12/12 12:55
 * @description :
 */
public class UserAdapter {

    public static User buildUser(String openId) {
        User user = new User();
        user.setOpenId(openId);
        return user;
    }
    public static User buildUserSave(String openId) {
        return User.builder().openId(openId).build();
    }

    public static User buildAuthorizeUser(Long id, WxOAuth2UserInfo userInfo) {
        User user = new User();
        user.setId(id);
        user.setName(userInfo.getNickname());
        user.setAvatar(userInfo.getHeadImgUrl());
        user.setSex(userInfo.getSex());
        if (userInfo.getNickname().length() > 6) {
            user.setName("名字过长" + RandomUtil.randomInt(100000));
        } else {
            user.setName(userInfo.getNickname());
        }
        return user;
    }

    public static UserInfoResp buildUserInfoResp(User userInfo, Integer countByValidItemId) {
        UserInfoResp userInfoResp = new UserInfoResp();
        BeanUtil.copyProperties(userInfo, userInfoResp);
        userInfoResp.setModifyNameChance(countByValidItemId);
        return userInfoResp;
    }

    public static UserInfoResp buildUserInfo(User user, Integer modifyNameCount) {
        UserInfoResp vo = new UserInfoResp();
        BeanUtil.copyProperties(user , vo);
        vo.setModifyNameChance(modifyNameCount);
        return vo;
    }

    /**
     * 固定的入参转成指定的出参
     * @param itemConfigs 所有的徽章列表
     * @param backpacks 用户所拥有的徽章李彪
     * @param user 当前查询用户
     * @return 当前用户佩戴的徽章
     */
    public static List<BadgeResp> buildBadgeResp(List<ItemConfig> itemConfigs, List<UserBackpack> backpacks, User user) {

        if (ObjectUtil.isNull(user)) {
            // 这里 user 入参可能为空，防止 NPE 问题
            return Collections.emptyList();
        }

        Set<Long> obtainItemSet = backpacks.stream().map(UserBackpack::getItemId).collect(Collectors.toSet());
        return itemConfigs.stream().map(a -> {
                    BadgeResp resp = new BadgeResp();
                    BeanUtils.copyProperties(a , resp);
                    resp.setObtain(obtainItemSet.contains(a.getId()) ? YesOrNoEnum.YES.getStatus() : YesOrNoEnum.NO.getStatus());
                    // 我是否有佩戴
                    resp.setWearing(Objects.equals(a.getId(), user.getItemId()) ? YesOrNoEnum.YES.getStatus() : YesOrNoEnum.NO.getStatus());
                    return resp;
                }).sorted(Comparator.comparing(BadgeResp::getWearing, Comparator.reverseOrder())
                        .thenComparing(BadgeResp::getObtain, Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

    /**
     * 用户佩戴徽章
     * @param uid 用户Id
     * @param itemId 徽章Id
     */
    /*public static void buildWearingBadge(Long uid, Long itemId) {
        User.builder()
                .id(uid)
                .itemId(itemId)
                .build();
    }*/
}
