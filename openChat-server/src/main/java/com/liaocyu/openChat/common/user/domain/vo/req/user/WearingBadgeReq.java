package com.liaocyu.openChat.common.user.domain.vo.req.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2023/12/21 14:03
 * @description :
 */
@Data
public class WearingBadgeReq {

    @ApiModelProperty("徽章id")
    @NotBlank
    private Long itemId;


}
