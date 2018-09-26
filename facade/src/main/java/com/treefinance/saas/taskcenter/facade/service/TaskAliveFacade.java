package com.treefinance.saas.taskcenter.facade.service;

import com.treefinance.saas.taskcenter.facade.result.common.TaskResult;

import java.util.Date;

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
     * 更新任务最近活跃时间
     *
     * @param taskId 任务id
     * @param date   时间
     * @return
     */
    TaskResult<Void> updateTaskActiveTime(Long taskId, Date date);

    /**
     * 获取任务最近活跃时间
     *
     * @param taskId 任务id
     * @return
     */
    TaskResult<String> getTaskAliveTime(Long taskId);

    /**
     * 删除任务最近活跃时间
     *
     * @param taskId
     * @return
     */
    TaskResult<Void> deleteTaskAliveTime(Long taskId);
}
