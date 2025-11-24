package com.lqf.ai.robot.model.vo.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author: 李启仿
 * @date: 2025/11/23
 * @description: 查询对话历史消息
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindChatHistoryMessagePageListRspVO {
    /**
     * 消息 ID
     */
    private Long id;
    /**
     * 对话 ID
     */
    private String chatId;
    /**
     * 内容
     */
    private String content;
    /**
     * 消息类型
     */
    private String role;
    /**
     * 发布时间
     */
    private LocalDateTime createTime;

}
