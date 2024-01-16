package com.liaocyu.openChat.common.chat.dao;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.liaocyu.openChat.common.chat.domain.entity.GroupMember;
import com.liaocyu.openChat.common.chat.domain.enums.GroupRoleAPPEnum;
import com.liaocyu.openChat.common.chat.domain.enums.GroupRoleEnum;
import com.liaocyu.openChat.common.chat.mapper.GroupMemberMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liaocyu.openChat.common.chat.service.cache.GroupMemberCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.liaocyu.openChat.common.chat.domain.enums.GroupRoleEnum.ADMIN_LIST;

/**
 * <p>
 * 群成员表 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/liaocyu">liaocyu</a>
 * @since 2024-01-03
 */
@Service
public class GroupMemberDao extends ServiceImpl<GroupMemberMapper, GroupMember> {

    @Autowired
    @Lazy
    private GroupMemberCache groupMemberCache;

    /**
     *
     * @param groupId 群主Id == 群聊表Id
     * @return
     */
    public List<Long> getMemberUidList(Long groupId) {
        List<GroupMember> list = lambdaQuery()
                .eq(GroupMember::getGroupId, groupId)
                .select(GroupMember::getUid)
                .list();
        return list.stream().map(GroupMember::getUid).collect(Collectors.toList());
    }

    /**
     *
     * @param groupId 群聊房间表id
     * @param uid uid
     * @return 群聊房间人数
     */
    public GroupMember getMember(Long groupId, Long uid) {
        return lambdaQuery()
                .eq(GroupMember::getGroupId , groupId)
                .eq(GroupMember::getUid , uid)
                .one();
    }

    /**
     *判断用户是否是群主
     * @param id 群组ID
     * @param uid 用户ID
     * @return 是否是群主
     */
    public boolean isLord(Long id, Long uid) {
        GroupMember groupMember = this.lambdaQuery()
                .eq(GroupMember::getGroupId , id) // 群组-房间Id
                .eq(GroupMember::getUid , uid) // uid
                .eq(GroupMember::getRole , GroupRoleEnum.LEADER.getType())
                .one();
        return ObjectUtil.isNotNull(groupMember);
    }

    /**
     * 是否是管理员
     *
     * @param id 群主Id
     * @param uid 用户Id
     * @return 是否是管理员
     */
    public boolean isManager(Long id, Long uid) {
        GroupMember groupMember = this.lambdaQuery()
                .eq(GroupMember::getGroupId , id)
                .eq(GroupMember::getUid , uid)
                .eq(GroupMember::getRole , GroupRoleEnum.MANAGER.getType())
                .one();

        return ObjectUtil.isNotNull(groupMember);
    }

    /**
     * 判断用户是否在房间中
     *
     * @param roomId 房间Id
     * @param uidList 用户Id
     * @return 是否在群聊中
     */
    public Boolean isGroupShip(Long roomId, List<Long> uidList) {
        List<Long> memberUidList = groupMemberCache.getMemberUidList(roomId);
        return memberUidList.containsAll(uidList);
    }


    /**
     * 根据群组Id删除群成员
     *
     * @param id 群组Id
     * @param uidList 群成员列表
     * @return 是否删除成功
     */
    public Boolean removeByGroupId(Long id , List<Long> uidList) {
        if (CollectionUtil.isNotEmpty(uidList)) {
            LambdaQueryWrapper<GroupMember> wrapper = new QueryWrapper<GroupMember>()
                    .lambda()
                    .eq(GroupMember::getGroupId, id)
                    .in(GroupMember::getUid, uidList);
            return this.remove(wrapper);
        }
        return false;
    }

    public List<GroupMember> getSelfGroup(Long uid) {
        return lambdaQuery()
                .eq(GroupMember::getUid, uid)
                .eq(GroupMember::getRole, GroupRoleEnum.LEADER.getType())
                .list();
    }

    public List<Long> getMemberBatch(Long groupId, List<Long> uidList) {
        List<GroupMember> list = lambdaQuery()
                .eq(GroupMember::getGroupId, groupId)
                .in(GroupMember::getUid, uidList)
                .select(GroupMember::getUid)
                .list();
        return list.stream().map(GroupMember::getUid).collect(Collectors.toList());
    }

    /**
     * 获取管理员uid列表
     *
     * @param id 群组ID
     * @return 管理员uid列表
     */
    public List<Long> getManageUidList(Long id) {
        return this.lambdaQuery()
                .eq(GroupMember::getGroupId, id)
                .eq(GroupMember::getRole, GroupRoleEnum.MANAGER.getType())
                .list()
                .stream()
                .map(GroupMember::getUid)
                .collect(Collectors.toList());
    }

    /**
     * 在群里面设置管理员 update
     *
     * @param id      群组ID
     * @param uidList 用户列表
     */
    public void addAdmin(Long id, List<Long> uidList) {
        LambdaUpdateWrapper<GroupMember> wrapper = new UpdateWrapper<GroupMember>().lambda()
                .eq(GroupMember::getGroupId, id)
                .in(GroupMember::getUid, uidList)
                .set(GroupMember::getRole, GroupRoleEnum.MANAGER.getType());
        this.update(wrapper);
    }

    /**
     * 撤销管理员
     *
     * @param id      群组ID
     * @param uidList 用户列表
     */
    public void revokeAdmin(Long id, List<Long> uidList) {
        LambdaUpdateWrapper<GroupMember> wrapper = new UpdateWrapper<GroupMember>().lambda()
                .eq(GroupMember::getGroupId, id)
                .in(GroupMember::getUid, uidList)
                .set(GroupMember::getRole, GroupRoleEnum.MEMBER.getType());
        this.update(wrapper);
    }

    /**
     * 批量获取成员群角色
     *
     * @param groupId 群ID
     * @param uidList 用户列表
     * @return 成员群角色列表
     */
    public Map<Long, Integer> getMemberMapRole(Long groupId, List<Long> uidList) {
        List<GroupMember> list = lambdaQuery()
                .eq(GroupMember::getGroupId, groupId)
                .in(GroupMember::getUid, uidList)
                .in(GroupMember::getRole, ADMIN_LIST)
                .select(GroupMember::getUid, GroupMember::getRole)
                .list();
        return list.stream().collect(Collectors.toMap(GroupMember::getUid, GroupMember::getRole));
    }
}
