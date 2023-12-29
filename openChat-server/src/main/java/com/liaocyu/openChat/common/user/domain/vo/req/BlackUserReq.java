package com.liaocyu.openChat.common.user.domain.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2023/12/21 14:03
 * @description :
 * 拉黑用户的请求
 */
@Data
public class BlackUserReq {

    @ApiModelProperty("拉黑用户")
    @NotBlank
    private Long uid;


}
