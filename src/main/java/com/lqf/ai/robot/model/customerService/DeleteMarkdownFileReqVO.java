package com.lqf.ai.robot.model.customerService;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: 李启仿
 * @date: 2025/11/25
 * @description: 删除问答文件
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeleteMarkdownFileReqVO {

    @NotNull(message = "问答文件 ID 不能为空")
    private Long id;

}