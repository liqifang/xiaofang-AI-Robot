package com.lqf.ai.robot.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: 李启仿
 * @date: 2025/11/23
 * @description:
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchResultDTO {

    /**
     * 页面访问链接
     */
    private String url;

    /**
     * 相关性评分
     */
    private Double score;

    /**
     * 页面内容
     */
    private String content;
}
