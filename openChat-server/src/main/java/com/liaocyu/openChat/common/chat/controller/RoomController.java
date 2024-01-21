package com.liaocyu.openChat.common.chat.controller;

import com.liaocyu.openChat.common.chat.domain.vo.req.*;
import com.liaocyu.openChat.common.chat.domain.vo.req.admin.AdminAddReq;
import com.liaocyu.openChat.common.chat.domain.vo.req.admin.AdminRevokeReq;
import com.liaocyu.openChat.common.chat.domain.vo.req.member.MemberAddReq;
import com.liaocyu.openChat.common.chat.domain.vo.req.member.MemberDelReq;
import com.liaocyu.openChat.common.chat.domain.vo.req.member.MemberExitReq;
import com.liaocyu.openChat.common.chat.domain.vo.req.member.MemberReq;
import com.liaocyu.openChat.common.chat.domain.vo.resp.ChatMemberListResp;
import com.liaocyu.openChat.common.chat.domain.vo.resp.ChatMemberResp;
import com.liaocyu.openChat.common.chat.domain.vo.resp.IdRespVO;
import com.liaocyu.openChat.common.chat.domain.vo.resp.MemberResp;
import com.liaocyu.openChat.common.chat.service.GroupMemberService;
import com.liaocyu.openChat.common.chat.service.RoomAppService;
import com.liaocyu.openChat.common.common.domain.vo.req.IdReqVO;
import com.liaocyu.openChat.common.common.domain.vo.resp.ApiResult;
import com.liaocyu.openChat.common.common.domain.vo.resp.CursorPageBaseResp;
import com.liaocyu.openChat.common.common.utils.RequestHolder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/10 10:08
 * @description :
 */
@RestController
@RequestMapping("/capi/room")
@Api(tags = "聊天室相关接口")
@Slf4j
public class RoomController {

    private final RoomAppService roomAppService;
    private final GroupMemberService groupMemberService;

    @Autowired
    public RoomController(RoomAppService roomAppService , GroupMemberService groupMemberService) {
        this.roomAppService = roomAppService;
        this.groupMemberService = groupMemberService;
    }

    /**
     * 返回当前用户所看到的群组详情
     * @param request
     * @return
     */
    @GetMapping("public/group")
    @ApiOperation("群组详情")
    public ApiResult<MemberResp> groupDetail(@Valid IdReqVO request) {
        Long uid = RequestHolder.get().getUid();
        return ApiResult.success(roomAppService.getGroupDetail(uid , request.getId())); // request.getId() 群聊房间表Id
    }

    /**
     * 返回群成员列表列表信息
     * TODO 注：这里默认看到的是热点群聊信息
     * @param request 房间表Id
     * @return
     */
    @GetMapping("public/group/member/page")
    @ApiOperation("群成员列表")
    public ApiResult<CursorPageBaseResp<ChatMemberResp>> getMemberPage(@Valid MemberReq request) {
        return ApiResult.success(roomAppService.getMemberPage(request));
    }

    @GetMapping("group/member/list")
    @ApiOperation("房间内的所有群成员列表-@专用")
    public ApiResult<List<ChatMemberListResp>> getMemberList(@Valid ChatMessageMemberReq request) {
        return ApiResult.success(roomAppService.getMemberList(request));
    }

    @DeleteMapping("group/member")
    @ApiOperation("移除成员")
    public ApiResult<Void> delMember(@Valid @RequestBody MemberDelReq request) {
        Long uid = RequestHolder.get().getUid();
        roomAppService.delMember(uid , request);
        return ApiResult.success();
    }

    @DeleteMapping("group/member/exit")
    @ApiOperation("退出群聊")
    public ApiResult<Boolean> exitGroup(@Valid @RequestBody MemberExitReq request) {
        Long uid = RequestHolder.get().getUid();
        groupMemberService.exitGroup(uid, request);
        return ApiResult.success();
    }

    /**
     * IdRespVO 群组Id
     * @param request 被邀请的uid列表
     * @caution 注意，这里每个人只能创建一个群聊
     * @return
     */
    @PostMapping("group")
    @ApiOperation("新增群组")
    public ApiResult<IdRespVO> addGroup(@Valid @RequestBody GroupAddReq request) {
        Long uid = RequestHolder.get().getUid();
        Long roomId = roomAppService.addGroup(uid, request);
        return ApiResult.success(IdRespVO.id(roomId));
    }

    /**
     * 邀请好友接口
     * @param request 房间Id、被邀请的好友Uid列表
     * @return
     */
    @PostMapping("group/member")
    @ApiOperation("邀请好友")
    public ApiResult<Void> addMember(@Valid @RequestBody MemberAddReq request) {
        Long uid = RequestHolder.get().getUid();
        roomAppService.addMember(uid, request);
        return ApiResult.success();
    }

    /**
     *
     * @param request 添加管理员请求 roomId ， 添加管理员的Uid列表
     * @return
     */
    @PutMapping("group/admin")
    @ApiOperation("添加管理员")
    public ApiResult<Boolean> addAdmin(@Valid @RequestBody AdminAddReq request) {
        Long uid = RequestHolder.get().getUid();
        groupMemberService.addAdmin(uid, request);
        return ApiResult.success();
    }

    /**
     * 插销管理员
     * @param request 房间号 -- 批处理撤销的管理员的Uid列表
     * @return
     */
    @DeleteMapping("group/admin")
    @ApiOperation("撤销管理员")
    public ApiResult<Boolean> revokeAdmin(@Valid @RequestBody AdminRevokeReq request) {
        Long uid = RequestHolder.get().getUid();
        groupMemberService.revokeAdmin(uid, request);
        return ApiResult.success();
    }

}
