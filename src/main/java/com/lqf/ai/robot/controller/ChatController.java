package com.lqf.ai.robot.controller;

import com.lqf.ai.robot.advisor.CustomChatMemoryAdvisor;
import com.lqf.ai.robot.advisor.CustomStreamLoggerAndMessage2DBAdvisor;
import com.lqf.ai.robot.advisor.NetworkSearchAdvisor;
import com.lqf.ai.robot.aspect.ApiOperationLog;
import com.lqf.ai.robot.domain.mapper.ChatMessageMapper;
import com.lqf.ai.robot.model.vo.chat.*;
import com.lqf.ai.robot.service.ChatService;
import com.lqf.ai.robot.service.SearXNGService;
import com.lqf.ai.robot.service.SearchResultContentFetcherService;
import com.lqf.ai.robot.utils.PageResponse;
import com.lqf.ai.robot.utils.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
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

/**
 * @author: 李启仿
 * @date: 2025/11/22
 * @description: 对话
 */

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;
    private final ChatMessageMapper chatMessageMapper;
    private final TransactionTemplate transactionTemplate;
    private final SearXNGService searXNGService;
    private final SearchResultContentFetcherService searchResultContentFetcherService;
    @Value("${spring.ai.openai.base-url}")
    private String baseUrl;
    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    @PostMapping("/new")
    @ApiOperationLog(description = "新建对话")
    public Response<?> newChat(@RequestBody @Validated NewChatReqVO newChatReqVO) {
        return chatService.newChat(newChatReqVO);
    }

    /**
     * 流式对话
     * @return
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

        // 构建 ChatModel
        ChatModel chatModel = OpenAiChatModel.builder()
                .openAiApi(OpenAiApi.builder()
                        .baseUrl(baseUrl)
                        .apiKey(apiKey)
                        .build())
                .build();

        // Advisor 集合
        List<Advisor> advisors = new ArrayList<>();

        // 动态设置调用的模型名称、温度值
        // 动态创建 ChatClient
        ChatClient.ChatClientRequestSpec chatClientRequestSpec = ChatClient.create(chatModel)
                .prompt()
                // .advisors(advisors)
                .options(OpenAiChatOptions.builder()
                        .model(modelName)
                        .temperature(temperature)
                        .build())
                .user(userMessage); // 用户提示词

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

    @PostMapping("/message/list")
    @ApiOperationLog(description = "查询对话历史消息")
    public PageResponse<FindChatHistoryMessagePageListRspVO> findChatMessagePageList(@RequestBody @Validated FindChatHistoryMessagePageListReqVO findChatHistoryMessagePageListReqVO) {
        return chatService.findChatHistoryMessagePageList(findChatHistoryMessagePageListReqVO);
    }

    @PostMapping("/list")
    @ApiOperationLog(description = "查询历史对话")
    public PageResponse<FindChatHistoryPageListRspVO> findChatHistoryPageList(@RequestBody @Validated FindChatHistoryPageListReqVO findChatHistoryPageListReqVO) {
        return chatService.findChatHistoryPageList(findChatHistoryPageListReqVO);
    }

    @PostMapping("/summary/rename")
    @ApiOperationLog(description = "重命名对话摘要")
    public Response<?> renameChatSummary(@RequestBody @Validated RenameChatReqVO renameChatReqVO) {
        return chatService.renameChatSummary(renameChatReqVO);
    }

    @PostMapping("/delete")
    @ApiOperationLog(description = "删除对话")
    public Response<?> deleteChat(@RequestBody @Validated DeleteChatReqVO deleteChatReqVO) {
        return chatService.deleteChat(deleteChatReqVO);
    }
}