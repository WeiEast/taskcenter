package com.treefinance.saas.taskcenter.dao.mapper;

import com.treefinance.saas.taskcenter.dao.entity.TaskCallbackLog;

public interface TaskCallbackLogUpdateMapper {
    /**
     * 插入更新
     *
     * @param record
     * @return
     */
    int insertOrUpdateSelective(TaskCallbackLog record);

}