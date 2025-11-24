package com.lqf.ai.robot.enums;

import com.lqf.ai.robot.exception.BaseExceptionInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author: 李启仿
 * @date: 2025/11/22
 * @description: 响应异常码
 */

@Getter
@AllArgsConstructor
public enum ResponseCodeEnum implements BaseExceptionInterface {

    // ----------- 通用异常状态码 -----------
    SYSTEM_ERROR("10000", "出错啦，正在努力修复中..."),
    PARAM_NOT_VALID("10001", "参数错误"),

    // ----------- 业务异常状态码 -----------
    CHAT_NOT_EXISTED("20000", "此对话不存在"),
    ;

    // 异常码
    private String errorCode;
    // 错误信息
    private String errorMessage;

}
