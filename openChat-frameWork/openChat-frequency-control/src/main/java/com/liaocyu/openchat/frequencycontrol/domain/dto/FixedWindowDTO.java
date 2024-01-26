package com.liaocyu.openchat.frequencycontrol.domain.dto;
import lombok.Data;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/25 15:29
 * @description :限流策略定义
 */
@Data
public class FixedWindowDTO extends FrequencyControlDTO {

    /**
     * 频控时间范围，默认单位秒
     *
     * @return 时间范围
     */
    private Integer time;
}
