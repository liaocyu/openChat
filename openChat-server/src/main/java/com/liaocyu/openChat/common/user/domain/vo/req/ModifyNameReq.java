package com.liaocyu.openChat.common.user.domain.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2023/12/21 14:03
 * @description :
 */
@Data
public class ModifyNameReq {

    @ApiModelProperty("用户名")
    @NotBlank
    @Length(max = 6 , message = "用户名不可以取太长，不然记不住")
    private String name;


}
