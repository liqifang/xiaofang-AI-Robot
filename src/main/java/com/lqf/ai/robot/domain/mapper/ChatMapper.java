package com.lqf.ai.robot.domain.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lqf.ai.robot.domain.dos.ChatDO;

/**
 * @author: 李启仿
 * @date: 2025/11/22
 * @description:
 */

public interface ChatMapper extends BaseMapper<ChatDO> {

    default Page<ChatDO> selectPageList(Long current, Long size) {
        Page<ChatDO> page = new Page<>(current, size);

        LambdaQueryWrapper<ChatDO> chatDo = Wrappers.<ChatDO>lambdaQuery()
                .orderByDesc(ChatDO::getUpdateTime);

        return selectPage(page, chatDo);
    }
}
