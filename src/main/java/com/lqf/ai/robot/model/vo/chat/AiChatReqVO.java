package com.lqf.ai.robot.model.vo.chat;

import jakarta.validation.constraints.NotBlank;
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
@NoArgsConstructor
@AllArgsConstructor
public class AiChatReqVO {
    @NotBlank(message = "用户消息不能为空")
    private String message;

    /**
     * 对话 ID
     */
    private String chatId;

    /**
     * 模型名称
     */
    @NotBlank(message = "调用的 AI 大模型名称不能为空")
    private String modelName;

    /**
     * 联网搜索
     */
    private Boolean networkSearch = false;

    /**
     * 温度值：默认为0.7
     */
    private Double temperature = 0.7;
}
