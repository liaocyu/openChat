package com.liaocyu.openChat.common.user.controller;


import com.liaocyu.openChat.common.common.domain.vo.resp.ApiResult;
import com.liaocyu.openChat.common.common.interceptor.TokenInterceptor;
import com.liaocyu.openChat.common.common.utils.RequestHolder;
import com.liaocyu.openChat.common.user.domain.vo.resp.UserInfoResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

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

    @GetMapping("/userInfo")
    @ApiOperation("获取用户相关信息")
    public ApiResult<UserInfoResp> getUserInfo(HttpServletRequest request) {
        Long uid = RequestHolder.get().getUid();
        System.out.println(uid);
        return null;
    }

}

