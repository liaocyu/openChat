package com.liaocyu.openChat.common.user.service.impl;

import com.liaocyu.openChat.common.common.annotation.RedissonLock;
import com.liaocyu.openChat.common.common.domain.dto.ItemInfoDTO;
import com.liaocyu.openChat.common.common.domain.dto.SummeryInfoDTO;
import com.liaocyu.openChat.common.common.event.UserBlackEvent;
import com.liaocyu.openChat.common.common.event.UserRegisterEvent;
import com.liaocyu.openChat.common.common.utils.AssertUtil;
import com.liaocyu.openChat.common.user.dao.BlackDao;
import com.liaocyu.openChat.common.user.dao.ItemConfigDao;
import com.liaocyu.openChat.common.user.dao.UserBackpackDao;
import com.liaocyu.openChat.common.user.dao.UserDao;
import com.liaocyu.openChat.common.user.domain.entity.*;
import com.liaocyu.openChat.common.user.domain.enums.BlackTypeEnum;
import com.liaocyu.openChat.common.user.domain.enums.ItemEnum;
import com.liaocyu.openChat.common.user.domain.enums.ItemTypeEnum;
import com.liaocyu.openChat.common.user.domain.vo.req.user.BlackUserReq;
import com.liaocyu.openChat.common.user.domain.vo.req.user.ItemInfoReq;
import com.liaocyu.openChat.common.user.domain.vo.req.user.SummeryInfoReq;
import com.liaocyu.openChat.common.user.domain.vo.resp.BadgeResp;
import com.liaocyu.openChat.common.user.domain.vo.resp.UserInfoResp;
import com.liaocyu.openChat.common.user.service.UserService;
import com.liaocyu.openChat.common.user.service.adapter.UserAdapter;
import com.liaocyu.openChat.common.user.service.cache.ItemCache;
import com.liaocyu.openChat.common.user.service.cache.UserCache;
import com.liaocyu.openChat.common.user.service.cache.UserSummaryCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2023/12/12 13:34
 * @description :
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    UserDao userDao;
    @Autowired
    UserBackpackDao userBackpackDao;
    @Autowired
    ItemCache itemCache;
    @Autowired
    ItemConfigDao itemConfigDao;
    @Autowired
    ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    BlackDao blackDao;
    @Autowired
    UserCache userCache;
    @Autowired
    UserSummaryCache userSummaryCache;



    @Override
    @Transactional
    public Long register(User insert) {
        userDao.save(insert);
        // 发送物品
        // 发布用户注册的事件
        applicationEventPublisher.publishEvent(new UserRegisterEvent(this, insert));
        return insert.getId();
    }

    @Override
    public UserInfoResp getUserInfo(Long uid) {
        User user = userDao.getById(uid);
        Integer modifyNameCount = userBackpackDao.getCountByValidItemId(uid, ItemEnum.MODIFY_NAME_CARD.getId());

        return UserAdapter.buildUserInfo(user, modifyNameCount);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @RedissonLock(key = "#uid")
    public void modifyName(Long uid, String name) {
        User oldUser = userDao.getByName(name);
        AssertUtil.isEmpty(oldUser, "名字已经被占用了，请重新换个名字");
        UserBackpack userBackpack = userBackpackDao.getFirstValidItem(uid, ItemEnum.MODIFY_NAME_CARD.getId());
        AssertUtil.isNotEmpty(userBackpack, "你已经改过名字啦~");
        // 使用改名卡
        boolean success = userBackpackDao.useItem(userBackpack);
        if (success) {
            // 改名
            userDao.modifyName(uid, name);
        }
    }

    @Override
    public List<BadgeResp> badges(Long uid) {

        // 获取所有徽章列表
        /**
         * 因为所有的徽章 不是经常可变的
         * 因此使用的是本地缓存 ， 使用的是 Spring提供的 ，SpringCache ???
         * 这里是从 caffeine 里面获取所有的徽章
         */
        List<ItemConfig> itemConfigs = itemCache.getByType(ItemTypeEnum.BADGE.getType());
        // 查询用户拥有的徽章
        List<UserBackpack> backpacks = userBackpackDao.getByItemIds(uid, itemConfigs.stream().map(ItemConfig::getId).collect(Collectors.toList()));
        // 查询用户佩戴的徽章
        User user = userDao.getById(uid);

        return UserAdapter.buildBadgeResp(itemConfigs, backpacks, user);
    }


    /**
     * 用户佩戴徽章Id
     *
     * @param uid    用户Id
     * @param itemId 徽章Id
     */
    @Override
    public void wearingBadge(Long uid, Long itemId) {
        // 确保有徽章
        UserBackpack firstvalidItem = userBackpackDao.getFirstValidItem(uid, itemId);
        // 断言
        AssertUtil.isNotEmpty(firstvalidItem, "您还没有这个徽章 , 请先获得");
        // 因为用户背包表里面有改名卡和徽章 ，所以还需要判断这个ItemId 是否是徽章
        ItemConfig itemConfig = itemConfigDao.getById(firstvalidItem.getId());
        AssertUtil.equal(itemConfig.getType(), ItemTypeEnum.BADGE.getType(), "只有徽章才能佩戴");
        // 佩戴徽章
        userDao.wearingBadge(uid, itemId);
        /*UserAdapter.buildWearingBadge(uid , itemId);*/
    }

    /**
     * 拉黑用户
     *
     * @param req 用户相应的请求
     */
    @Override
    @Transactional
    public void black(BlackUserReq req) {
        // 获取请求里面的用户信息
        Long uid = req.getUid();
        Black user = new Black();
        user.setTarget(uid.toString());
        user.setType(BlackTypeEnum.UID.getType());
        blackDao.save(user);
        // 获取当前用户 封掉该用户的创建时候的createIp 和 updateIp
        User blackUser = userDao.getById(uid);
        balckIP(Optional.ofNullable(blackUser.getIpInfo()).map(IpInfo::getCreateIp).orElse(null));
        balckIP(Optional.ofNullable(blackUser.getIpInfo()).map(IpInfo::getUpdateIp).orElse(null));
        applicationEventPublisher.publishEvent(new UserBlackEvent(this, blackUser));
    }

    @Override
    public List<SummeryInfoDTO> getSummeryUserInfo(SummeryInfoReq req) {
        // 需要前端同步的uid
        List<Long> uidList = getNeedSyncUidList(req.getReqList());
        // 加载用户信息
        Map<Long , SummeryInfoDTO> batch = userSummaryCache.getBatch(uidList);
        return req.getReqList()
                .stream()
                .map(a -> batch.containsKey(a.getUid()) ? batch.get(a.getUid()) : SummeryInfoDTO.skip(a.getUid()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemInfoDTO> getItemInfo(ItemInfoReq req) {
        return req.getReqList().stream().map(a -> {
            ItemConfig itemConfig = itemCache.getById(a.getItemId());
            if (Objects.nonNull(a.getLastModifyTime()) && a.getLastModifyTime() >= itemConfig.getUpdateTime().getTime()) {
                return ItemInfoDTO.skip(a.getItemId());
            }
            ItemInfoDTO dto = new ItemInfoDTO();
            dto.setItemId(itemConfig.getId());
            dto.setImg(itemConfig.getImg());
            dto.setDescribe(itemConfig.getDescribe());
            return dto;
        }).collect(Collectors.toList());
    }

    private List<Long> getNeedSyncUidList(List<SummeryInfoReq.infoReq> reqList) {
        List<Long> needSyncUidList = new ArrayList<>();
        List<Long> userModifyTime = userCache.getUserModifyTime(reqList.stream().map(SummeryInfoReq.infoReq::getUid).collect(Collectors.toList()));
        for(int i = 0 ; i < reqList.size() ; i ++) {
            SummeryInfoReq.infoReq infoReq = reqList.get(i);
            Long modifyTime = userModifyTime.get(i);
            if (Objects.isNull(infoReq.getLastModifyTime()) || (Objects.nonNull(modifyTime) && modifyTime > infoReq.getLastModifyTime())) {
                needSyncUidList.add(infoReq.getUid());
            }
        }
        return needSyncUidList;
    }

    // 封禁相应用户的IP
    private void balckIP(String ip) {
        if (StringUtils.isBlank(ip)) {
            return;
        }
        try {
            Black user = new Black();
            user.setTarget(ip);
            user.setType(BlackTypeEnum.IP.getType());
            blackDao.save(user);
        } catch (Exception e) {
            log.error("duplicate black ip:{}", ip);
        }
    }
}
