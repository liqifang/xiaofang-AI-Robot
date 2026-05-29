package com.lqf.ai.robot.controller;


import com.lqf.ai.robot.advisor.CustomChatMemoryAdvisor;
import com.lqf.ai.robot.advisor.CustomStreamLoggerAndMessage2DBAdvisor;
import com.lqf.ai.robot.advisor.NetworkSearchAdvisor;
import com.lqf.ai.robot.aspect.ApiOperationLog;
import com.lqf.ai.robot.domain.mapper.ChatMessageMapper;
import com.lqf.ai.robot.model.vo.chat.AIResponse;
import com.lqf.ai.robot.model.vo.chat.AiChatReqVO;
import com.lqf.ai.robot.service.SearXNGService;
import com.lqf.ai.robot.service.SearchResultContentFetcherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.http.MediaType;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/chat")
public class NewChatController {

    private final ChatClient chatClient;
    private final ChatMessageMapper chatMessageMapper;
    private final TransactionTemplate transactionTemplate;
    private final SearXNGService searXNGService;
    private final SearchResultContentFetcherService searchResultContentFetcherService;

    public NewChatController(ChatClient.Builder chatClientBuilder,
                             ChatMessageMapper chatMessageMapper,
                             TransactionTemplate transactionTemplate,
                             SearXNGService searXNGService,
                             SearchResultContentFetcherService searchResultContentFetcherService) {
        this.chatClient = chatClientBuilder.build();
        this.chatMessageMapper = chatMessageMapper;
        this.transactionTemplate = transactionTemplate;
        this.searXNGService = searXNGService;
        this.searchResultContentFetcherService = searchResultContentFetcherService;
    }


    /**
     * 流式对话
     * @return 流式响应结果
     */
    @PostMapping(value = "/completion", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ApiOperationLog(description = "流式对话")
    public Flux<AIResponse> chat(@RequestBody @Validated AiChatReqVO aiChatReqVO) {
        // 用户消息
        String userMessage = aiChatReqVO.getMessage();
        // 模型名称
        String modelName = aiChatReqVO.getModelName();
        // 温度值
        Double temperature = aiChatReqVO.getTemperature();
        // 是否开启联网搜索
        boolean networkSearch = aiChatReqVO.getNetworkSearch();

        // Advisor 集合
        List<Advisor> advisors = new ArrayList<>();

        // 动态设置调用的模型名称、温度值
        // 动态创建 ChatClient
        ChatClient.ChatClientRequestSpec chatClientRequestSpec = chatClient.prompt()
                .options(OpenAiChatOptions.builder()
                        .model(modelName)
                        .temperature(temperature)
                        .build())
                .user(userMessage);

        // 是否开启了联网搜索
        if (networkSearch) {
            advisors.add(new NetworkSearchAdvisor(searXNGService, searchResultContentFetcherService));
        } else {
            // 添加自定义对话记忆 Advisor（以最新的 50 条消息作为记忆）
            advisors.add(new CustomChatMemoryAdvisor(chatMessageMapper, aiChatReqVO, 50));
        }

        // 添加自定义打印流式对话日志 Advisor
        advisors.add(new CustomStreamLoggerAndMessage2DBAdvisor(chatMessageMapper, aiChatReqVO, transactionTemplate));

        // 应用 Advisor 集合
        chatClientRequestSpec.advisors(advisors);

        // 流式输出
        return chatClientRequestSpec
                .stream()
                .content()
                .mapNotNull(text -> AIResponse.builder().v(text).build()); // 构建返参 AIResponse

    }
}
