package com.lqf.ai.robot.model.vo.chat;

import com.lqf.ai.robot.model.common.BasePageQuery;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: 李启仿
 * @date: 2025/11/23
 * @description: 查询对话历史消息
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindChatHistoryMessagePageListReqVO extends BasePageQuery {

    @NotBlank(message = "对话 ID 不能为空")
    private String chatId;
}