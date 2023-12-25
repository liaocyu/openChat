package com.liaocyu.openChat.common.user.controller;


import com.liaocyu.openChat.common.common.domain.vo.resp.ApiResult;
import com.liaocyu.openChat.common.common.interceptor.TokenInterceptor;
import com.liaocyu.openChat.common.common.utils.RequestHolder;
import com.liaocyu.openChat.common.user.domain.vo.req.ModifyNameReq;
import com.liaocyu.openChat.common.user.domain.vo.req.WearingBadgeReq;
import com.liaocyu.openChat.common.user.domain.vo.resp.BadgeResp;
import com.liaocyu.openChat.common.user.domain.vo.resp.UserInfoResp;
import com.liaocyu.openChat.common.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author <a href="https://github.com/liaocyu">liaocyu</a>
 * @since 2023-12-08
 */
@RestController
@RequestMapping("capi/user")
@Api(tags = "用户相关接口")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("userInfo")
    @ApiOperation("获取用户相关信息")
    public ApiResult<UserInfoResp> getUserInfo() {

        return ApiResult.success(userService.getUserInfo(RequestHolder.get().getUid()));
    }

    @PutMapping("name")
    @ApiOperation("修改用户名")
    public ApiResult<Void> modifyName(@RequestBody @Valid ModifyNameReq req) {

        userService.modifyName(RequestHolder.get().getUid() , req.getName());
        return ApiResult.success();
    }

    @GetMapping("badges")
    @ApiOperation("可选徽章预览")
    public ApiResult<List<BadgeResp>> badges() {

        return ApiResult.success(userService.badges(RequestHolder.get().getUid()));
    }

    @PutMapping("badge")
    @ApiOperation("佩戴徽章")
    public ApiResult<Void> wearingBadge(@Valid @RequestBody WearingBadgeReq req) {
        userService.wearingBadge(RequestHolder.get().getUid() , req.getItemId());
        return ApiResult.success();
    }








}

