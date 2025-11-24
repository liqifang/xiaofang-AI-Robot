package com.lqf.ai.robot.model.vo.chat;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: 李启仿
 * @date: 2025/11/22
 * @description: 新建对话
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewChatReqVO {

    @NotBlank(message = "用户消息不能为空")
    private String message;
}
