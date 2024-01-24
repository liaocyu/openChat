package com.liaocyu.openChat.common.chat.service;

import com.liaocyu.openChat.common.chat.domain.vo.req.admin.AdminAddReq;
import com.liaocyu.openChat.common.chat.domain.vo.req.admin.AdminRevokeReq;
import com.liaocyu.openChat.common.chat.domain.vo.req.member.MemberExitReq;

/**
 * <p>
 * 群成员表 服务类
 * </p>
 *
 * @author <a href="https://github.com/liaocyu">liaocyu</a>
 * @since 2024-01-03
 */
public interface GroupMemberService {

    /**
     * 退出群聊
     *
     * @param uid     用户ID
     * @param request 请求信息
     */
    void exitGroup(Long uid, MemberExitReq request);
    /**
     * 增加管理员
     *
     * @param uid     用户ID
     * @param request 请求信息
     */
    void addAdmin(Long uid, AdminAddReq request);

    /**
     * 撤销管理员
     *
     * @param uid     用户ID
     * @param request 请求信息
     */
    void revokeAdmin(Long uid, AdminRevokeReq request);

}
