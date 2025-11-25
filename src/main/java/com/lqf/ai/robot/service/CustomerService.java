package com.lqf.ai.robot.service;

import com.lqf.ai.robot.model.customerService.DeleteMarkdownFileReqVO;
import com.lqf.ai.robot.model.customerService.FindMarkdownFilePageListReqVO;
import com.lqf.ai.robot.model.customerService.FindMarkdownFilePageListRspVO;
import com.lqf.ai.robot.model.customerService.UpdateMarkdownFileReqVO;
import com.lqf.ai.robot.utils.PageResponse;
import com.lqf.ai.robot.utils.Response;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author: 李启仿
 * @date: 2025/11/25
 * @description: AI 客服
 */

public interface CustomerService {

    /**
     * 上传 Markdown 问答文件
     * @param file
     * @return
     */
    Response<?> uploadMarkdownFile(MultipartFile file);

    /**
     * 删除 Markdown 问答文件
     * @param deleteMarkdownFileReqVO
     * @return
     */
    Response<?> deleteMarkdownFile(DeleteMarkdownFileReqVO deleteMarkdownFileReqVO);

    /**
     * 分页查询 Markdown 问答文件
     * @param findMarkdownFilePageListReqVO
     * @return
     */
    PageResponse<FindMarkdownFilePageListRspVO> findMarkdownFilePageList(FindMarkdownFilePageListReqVO findMarkdownFilePageListReqVO);

    /**
     * 修改  Markdown 问答文件信息
     * @param updateMarkdownFileReqVO
     * @return
     */
    Response<?> updateMarkdownFile(UpdateMarkdownFileReqVO updateMarkdownFileReqVO);
}
