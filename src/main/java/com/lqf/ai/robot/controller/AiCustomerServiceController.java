package com.lqf.ai.robot.controller;

import com.lqf.ai.robot.advisor.CustomerServiceAdvisor;
import com.lqf.ai.robot.aspect.ApiOperationLog;
import com.lqf.ai.robot.model.customerService.*;
import com.lqf.ai.robot.service.CustomerService;
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
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: 李启仿
 * @date: 2025/11/25
 * @description: AI 客服
 */

@RestController
@RequestMapping("/customer-service")
@Slf4j
@RequiredArgsConstructor
public class AiCustomerServiceController {

    private final CustomerService customerService;
    private final VectorStore vectorStore;

    @Value("${customer-service.model}")
    private String model;
    @Value("${customer-service.temperature}")
    private Double temperature;
    @Value("${spring.ai.openai.base-url}")
    private String baseUrl;
    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    /**
     * 问答 MD 文件上传
     * @param file
     * @return
     */
    @PostMapping(value = "/md/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Response<?> uploadMarkdownFile(@RequestPart(value = "file", required = false) MultipartFile file) {
        return customerService.uploadMarkdownFile(file);
    }

    @PostMapping("/md/delete")
    @ApiOperationLog(description = "删除 Markdown 问答文件")
    public Response<?> deleteMarkdownFile(@RequestBody @Validated DeleteMarkdownFileReqVO deleteMarkdownFileReqVO) {
        return customerService.deleteMarkdownFile(deleteMarkdownFileReqVO);
    }

    @PostMapping("/md/list")
    @ApiOperationLog(description = "Markdown 问答文件分页查询")
    public PageResponse<FindMarkdownFilePageListRspVO> findMarkdownFilePageList(@RequestBody @Validated FindMarkdownFilePageListReqVO findMarkdownFilePageListReqVO) {
        return customerService.findMarkdownFilePageList(findMarkdownFilePageListReqVO);
    }

    @PostMapping("/md/update")
    @ApiOperationLog(description = "修改 Markdown 问答文件信息")
    public Response<?> updateMarkdownFile(@RequestBody @Validated UpdateMarkdownFileReqVO updateMarkdownFileReqVO) {
        return customerService.updateMarkdownFile(updateMarkdownFileReqVO);
    }

    /**
     * 流式对话
     * @return
     */
    // 测试用
    // @GetMapping(value = "/chat/completion", produces = "text/html;charset=utf-8")
    // @ApiOperationLog(description = "AI 智能客服对话")
    // public Flux<String> chat(@RequestParam(value = "message") String userMessage) {
    @PostMapping(value = "/chat/completion", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ApiOperationLog(description = "AI 智能客服对话")
    public Flux<String> chat(@RequestBody @Validated AiCustomerServiceChatReqVO aiChatReqVO) {
        // 用户消息
        String userMessage = aiChatReqVO.getMessage();

        // 构建 ChatModel
        ChatModel chatModel = OpenAiChatModel.builder()
                .openAiApi(OpenAiApi.builder()
                        .baseUrl(baseUrl)
                        .apiKey(apiKey)
                        .build())
                .build();

        // 动态设置调用的模型名称、温度值
        ChatClient.ChatClientRequestSpec chatClientRequestSpec = ChatClient.create(chatModel)
                .prompt()
                .options(OpenAiChatOptions.builder()
                        .model(model)
                        .temperature(temperature)
                        .build())
                .user(userMessage); // 用户提示词

        // Advisor 集合
        List<Advisor> advisors = new ArrayList<>();
        advisors.add(new CustomerServiceAdvisor(vectorStore)); // 检索向量库，组合增强提示词

        // 应用 Advisor 集合
        chatClientRequestSpec.advisors(advisors);

        // 流式输出
        return chatClientRequestSpec
                .stream()
                .content();
    }

}