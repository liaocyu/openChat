package com.liaocyu.openChat.common.user.domain.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2023/12/18 15:31
 * @description :
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoResp {
    @ApiModelProperty(value = "uid")
    private Long id;
    @ApiModelProperty(value = "用户名称")
    private String name; // 用户名称
    @ApiModelProperty(value = "用户头像")
    private String avatar; // 用户头像
    @ApiModelProperty(value = "用户性别")
    private Integer sex; // 用户性别
    @ApiModelProperty(value = "剩余改名次数")
    private Integer modifyNameChance; // 用户改名卡剩余次数
}
