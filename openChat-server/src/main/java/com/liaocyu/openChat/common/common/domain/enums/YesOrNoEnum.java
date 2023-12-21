package com.liaocyu.openChat.common.common.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2023/12/19 14:46
 * @description :
 */
@AllArgsConstructor
@Getter
public enum YesOrNoEnum {
    NO( 0 , "否") ,
    YES(1 , "是") , ;

    private final Integer status ;
    private final String desc;

}
