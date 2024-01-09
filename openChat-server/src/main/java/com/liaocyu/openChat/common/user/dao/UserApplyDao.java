package com.liaocyu.openChat.common.user.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liaocyu.openChat.common.user.domain.entity.UserApply;
import com.liaocyu.openChat.common.user.domain.enums.ApplyStatusEnum;
import com.liaocyu.openChat.common.user.domain.enums.ApplyTypeEnum;
import com.liaocyu.openChat.common.user.mapper.UserApplyMapper;
import com.liaocyu.openChat.common.user.service.IUserApplyService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.liaocyu.openChat.common.user.domain.enums.ApplyReadStatusEnum.READ;
import static com.liaocyu.openChat.common.user.domain.enums.ApplyReadStatusEnum.UNREAD;
import static com.liaocyu.openChat.common.user.domain.enums.ApplyStatusEnum.AGREE;

/**
 * <p>
 * 用户申请表 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/liaocyu">liaocyu</a>
 * @since 2024-01-03
 */
@Service
public class UserApplyDao extends ServiceImpl<UserApplyMapper, UserApply> {

    /**
     * 获取我的好友申请列表
     * @param uid uid
     * @param targetUid 目标用户 uid
     * @return
     */
    public UserApply getFriendApproving(Long uid, Long targetUid) {
        return lambdaQuery()
                .eq(UserApply::getUid, uid) // 申请人 Uid
                .eq(UserApply::getTargetId, targetUid) // 接收人 Uid
                .eq(UserApply::getStatus , ApplyStatusEnum.WAIT_APPROVAL) // 申请状态 - 待审批
                .eq(UserApply::getType , ApplyTypeEnum.ADD_FRIEND.getCode()) // 申请状态码 - 加好友
                .one();
    }

    public void agree(Long applyId) {
        lambdaUpdate()
                .set(UserApply::getStatus , AGREE.getCode())
                .eq(UserApply::getId , applyId)
                .update();
    }

    public IPage<UserApply> friendApplyPage(Long uid, Page page) {
        return lambdaQuery()
                .eq(UserApply::getTargetId , uid)
                .eq(UserApply::getType , ApplyTypeEnum.ADD_FRIEND.getCode())
                .orderByDesc(UserApply::getCreateTime)
                .page(page);
    }

    public void readApples(Long uid, List<Long> applyIds) {
        lambdaUpdate()
                .set(UserApply::getReadStatus , READ.getCode())
                .eq(UserApply::getReadStatus , UNREAD.getCode())
                .in(UserApply::getId , applyIds)
                .eq(UserApply::getTargetId , uid)
                .update();
    }

    public Integer getUnReadCount(Long targetId) {
        return lambdaQuery().eq(UserApply::getTargetId , targetId)
                .eq(UserApply::getReadStatus, UNREAD.getCode())
                .count();
    }
}
