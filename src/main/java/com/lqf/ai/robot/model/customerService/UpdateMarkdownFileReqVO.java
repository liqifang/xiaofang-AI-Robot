package com.lqf.ai.robot.model.customerService;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: 李启仿
 * @date: 2025/11/25
 * @description: 修改 Markdown 问答文件
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateMarkdownFileReqVO {

    @NotNull(message = "问答文件 ID 不能为空")
    private Long id;

    @NotBlank(message = "备注信息不能为空")
    private String remark;

}