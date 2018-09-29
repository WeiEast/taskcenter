package com.treefinance.saas.taskcenter.facade.service;

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

    TaskResult<Void> handleTaskTimeout(Long taskId);

    TaskResult<Void> handleTaskAliveTimeout(Long taskId, Date startTime);

}
