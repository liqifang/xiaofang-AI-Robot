package com.lqf.ai.robot.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lqf.ai.robot.domain.dos.ChatDO;
import com.lqf.ai.robot.domain.dos.ChatMessageDO;
import com.lqf.ai.robot.domain.mapper.ChatMapper;
import com.lqf.ai.robot.domain.mapper.ChatMessageMapper;
import com.lqf.ai.robot.enums.ResponseCodeEnum;
import com.lqf.ai.robot.exception.BizException;
import com.lqf.ai.robot.model.vo.chat.*;
import com.lqf.ai.robot.service.ChatService;
import com.lqf.ai.robot.utils.PageResponse;
import com.lqf.ai.robot.utils.Response;
import com.lqf.ai.robot.utils.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * @author: 李启仿
 * @date: 2025/11/22
 * @description: 对话
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService{

    private final ChatMapper chatMapper;
    private final ChatMessageMapper chatMessageMapper;

    @Override
    public Response<NewChatRspVO> newChat(NewChatReqVO newChatReqVO) {
        // 用户发送的消息
        String message = newChatReqVO.getMessage();

        // UUID
        String uuid = UUID.randomUUID().toString();
        // 截取对话摘要
        String summary = StringUtil.truncate(message, 20);

        // 插入数据库
        chatMapper.insert(ChatDO.builder()
                        .summary(summary)
                        .uuid(uuid)
                        .createTime(LocalDateTime.now())
                        .updateTime(LocalDateTime.now())
                        .build()
        );

        // 前端返参
        return Response.success(NewChatRspVO.builder()
                        .summary(summary)
                        .uuid(uuid)
                        .build()
        );
    }

    /**
     * 查询历史消息
     *
     * @param findChatHistoryMessagePageListReqVO
     * @return
     */
    @Override
    public PageResponse<FindChatHistoryMessagePageListRspVO> findChatHistoryMessagePageList(FindChatHistoryMessagePageListReqVO findChatHistoryMessagePageListReqVO) {
        // 获取请求参数
        String chatId = findChatHistoryMessagePageListReqVO.getChatId();
        Long current = findChatHistoryMessagePageListReqVO.getCurrent();
        Long size = findChatHistoryMessagePageListReqVO.getSize();

        // 执行分析查询
        Page<ChatMessageDO> chatMessageDOPage = chatMessageMapper.selectPageList(current, size, chatId);

        List<ChatMessageDO> chatMessageDOS = chatMessageDOPage.getRecords();
        // DO 转 VO
        List<FindChatHistoryMessagePageListRspVO> vos = null;
        if (CollUtil.isNotEmpty(chatMessageDOS)) {
            vos = chatMessageDOS.stream()
                    .map(chatMessageDO -> FindChatHistoryMessagePageListRspVO.builder()
                            .id(chatMessageDO.getId())
                            .chatId(chatMessageDO.getChatUuid())
                            .role(chatMessageDO.getRole())
                            .content(chatMessageDO.getContent())
                            .createTime(chatMessageDO.getCreateTime())
                            .build())
                    .sorted(Comparator.comparing(FindChatHistoryMessagePageListRspVO::getCreateTime))
                    .toList();
        }

        return PageResponse.success(chatMessageDOPage, vos);
    }

    /**
     * 查询历史对话
     *
     * @param findChatHistoryPageListReqVO
     * @return
     */
    @Override
    public PageResponse<FindChatHistoryPageListRspVO> findChatHistoryPageList(FindChatHistoryPageListReqVO findChatHistoryPageListReqVO) {
        Long current = findChatHistoryPageListReqVO.getCurrent();
        Long size = findChatHistoryPageListReqVO.getSize();

        Page<ChatDO> chatDOPage = chatMapper.selectPageList(current, size);

        // DO转VO
        List<ChatDO> chatDOS = chatDOPage.getRecords();
        List<FindChatHistoryPageListRspVO> vos = null;
        if (CollUtil.isNotEmpty(chatDOS)) {
            vos = chatDOS.stream()
                    .map(chatDO -> FindChatHistoryPageListRspVO.builder()
                            .id(chatDO.getId())
                            .uuid(chatDO.getUuid())
                            .summary(chatDO.getSummary())
                            .updateTime(chatDO.getUpdateTime())
                            .build())
                    .toList();
        }
        return PageResponse.success(chatDOPage, vos);
    }

    /**
     * 重命名对话摘要
     *
     * @param renameChatReqVO
     * @return
     */
    @Override
    public Response<?> renameChatSummary(RenameChatReqVO renameChatReqVO) {
        // 对话 ID
        Long chatId = renameChatReqVO.getId();
        // 摘要
        String summary = renameChatReqVO.getSummary();

        // 根据主键 ID 更新摘要
        chatMapper.updateById(ChatDO.builder()
                .id(chatId)
                .summary(summary)
                .build());

        return Response.success();
    }

    /**
     * 删除对话
     *
     * @param deleteChatReqVO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<?> deleteChat(DeleteChatReqVO deleteChatReqVO) {
        // 对话 UUID
        String uuid = deleteChatReqVO.getUuid();

        // 删除对话
        int count = chatMapper.delete(Wrappers.<ChatDO>lambdaQuery()
                .eq(ChatDO::getUuid, uuid));

        // 如果删除操作影响的行数为 0，说明想要删除的对话不存在
        if (count == 0) {
            throw new BizException(ResponseCodeEnum.CHAT_NOT_EXISTED);
        }

        // 批量删除对话下的所有消息
        chatMessageMapper.delete(Wrappers.<ChatMessageDO>lambdaQuery()
                .eq(ChatMessageDO::getChatUuid, uuid));

        return Response.success();
    }

}
