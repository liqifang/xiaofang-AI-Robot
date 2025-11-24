package com.lqf.ai.robot.model.vo.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: 李启仿
 * @date: 2025/11/4
 * @description: AI 对话响应
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AIResponse {
    // 流式响应内容
    private String v;
}