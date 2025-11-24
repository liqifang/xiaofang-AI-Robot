package com.lqf.ai.robot.config;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: 李启仿
 * @date: 2025/11/3
 * @description: ChatClient 配置
 */

@Configuration
public class ChatClientConfig {

    @Resource
    private ChatMemory chatMemory;

    /**
     * 初始化 ChatClient 客户端
     * @param chatModel
     * @return
     */
    @Bean
    public ChatClient chatClient(OpenAiChatModel chatModel) {
        return ChatClient.builder(chatModel)
                .build();
    }
}
