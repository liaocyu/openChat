package com.liaocyu.openChat.common.user.service;

import com.liaocyu.openChat.common.common.domain.dto.ItemInfoDTO;
import com.liaocyu.openChat.common.common.domain.dto.SummeryInfoDTO;
import com.liaocyu.openChat.common.user.domain.entity.User;
import com.liaocyu.openChat.common.user.domain.vo.req.user.BlackUserReq;
import com.liaocyu.openChat.common.user.domain.vo.req.user.ItemInfoReq;
import com.liaocyu.openChat.common.user.domain.vo.req.user.ModifyNameReq;
import com.liaocyu.openChat.common.user.domain.vo.req.user.SummeryInfoReq;
import com.liaocyu.openChat.common.user.domain.vo.resp.BadgeResp;
import com.liaocyu.openChat.common.user.domain.vo.resp.UserInfoResp;

import java.util.List;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author <a href="https://github.com/liaocyu">liaocyu</a>
 * @since 2023-12-08
 */
public interface UserService {

    /**
     * 用户注册
     *
     * @param insert 注册的用户
     * @return
     */
    Long register(User insert);

    /**
     * 获取用户个人信息
     * @param uid
     * @return
     */
    UserInfoResp getUserInfo(Long uid);

    /**
     * 用户修改用户名
     * @param uid 用户Id
     * @param name 用户修改的用户名
     */
    void modifyName(Long uid, String name);

    void modifyName(Long uid, ModifyNameReq req);

    List<BadgeResp> badges(Long uid);

    /**
     * 佩戴徽章
     * @param uid 用户Id
     * @param itemId 徽章Id
     */
    void wearingBadge(Long uid, Long itemId);

    /**
     * 拉黑用户
     *
     * @param req 用户相应的请求
     */
    void black(BlackUserReq req);

    /**
     * 获取用户的汇总信息
     *
     * @param req
     * @return
     */
    List<SummeryInfoDTO> getSummeryUserInfo(SummeryInfoReq req);

    List<ItemInfoDTO> getItemInfo(ItemInfoReq req);
}
