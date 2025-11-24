package com.lqf.ai.robot.service;


import com.lqf.ai.robot.model.dto.SearchResultDTO;

import java.util.List;

/**
 * @author: 李启仿
 * @date: 2025/11/20
 * @description:
 */

public interface SearXNGService {

    /**
     * 调用 SearXNG Api, 获取搜索结果
     * @param query 搜索关键词
     * @return
     */
    List<SearchResultDTO> search(String query);
}