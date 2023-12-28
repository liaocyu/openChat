package com.liaocyu.openChat.common.user.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2023/12/26 16:03
 * @description :
 * ip具体详情
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class IpDetail implements Serializable {
    /**
     * 用户Ip
     */
    private String ip;
    /**
     * 宽带厂家
     */
    private String isp;
    /**
     * 厂家编号
     */
    private String isp_id;
    /**
     * 用户城市
     */
    private String city;
    /**
     * 城市id
     */
    private String city_id;
    /**
     * 用户国家
     */
    private String country;
    /**
     * 国家id
     */
    private String country_id;
    /**
     * 用户地区
     */
    private String region;
    /**
     * 地区id
     */
    private String region_id;
}
