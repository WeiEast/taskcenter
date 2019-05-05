package com.treefinance.saas.taskcenter.facade.service;

import com.treefinance.saas.taskcenter.facade.response.TaskResponse;
import com.treefinance.saas.taskcenter.facade.result.common.TaskResult;

import java.util.Date;

/**
 * @author haojiahong
 * @date 2018/9/27
 */
public interface TaskTimeFacade {
    /**
     * 更新任务登录时间
     *
     * @param taskId
     * @param date
     * @return
     */
    TaskResult<Void> updateLoginTime(Long taskId, Date date);

    TaskResult<Date> getLoginTime(Long taskId);

    /**
     * @deprecated use {@link #processTaskIfTimeout(Long)} instead
     */
    @Deprecated
    TaskResult<Void> handleTaskTimeout(Long taskId);
    @Deprecated
    TaskResult<Void> handleTaskAliveTimeout(Long taskId, Date startTime);

    /**
     * 异步检测超时任务并处理
     * 
     * @param taskId 任务ID
     * @return Void
     */
    TaskResponse<Void> processTaskIfTimeout(Long taskId);

}
