package com.liaocyu.openChat.common.common.domain.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/12 11:17
 * @description : id req请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IdReqVO {
    @ApiModelProperty("id")
    @NotNull
    private Long id;
}

