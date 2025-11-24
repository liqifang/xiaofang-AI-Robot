package com.lqf.ai.robot.aspect;

import java.lang.annotation.*;

/**
 * @author: 李启仿
 * @date: 2025/11/22
 * @description: 自定义注解
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface ApiOperationLog {
    /**
     * API 功能描述
     * @return
     */
    String description() default "";
}
