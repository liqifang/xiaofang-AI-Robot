package com.lqf.ai.robot;

import com.lqf.ai.robot.domain.dos.ChatDO;
import com.lqf.ai.robot.domain.mapper.ChatMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author: 李启仿
 * @date: 2025/11/22
 * @description:
 */

@SpringBootTest
class MybatisPlusTests {

    @Resource
    private ChatMapper chatMapper;

    /**
     * 添加数据
     */
    @Test
    void testInsert() {
        chatMapper.insert(ChatDO.builder()
                .uuid(UUID.randomUUID().toString())
                .summary("新对话")
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build());
    }

}