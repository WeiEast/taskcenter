package com.treefinance.saas.taskcenter.facade.service;

import com.treefinance.saas.taskcenter.facade.result.common.TaskResult;

/**
 * @author haojiahong
 * @date 2018/9/21
 */
public interface TaskAliveFacade {

    /**
     * 更新任务最近活跃时间
     *
     * @param taskId 任务id
     * @return
     */
    TaskResult<Void> updateTaskActiveTime(Long taskId);

    /**
     * 获取任务最近活跃时间
     *
     * @param taskId 任务id
     * @return
     */
    TaskResult<String> getTaskAliveTime(Long taskId);

}
