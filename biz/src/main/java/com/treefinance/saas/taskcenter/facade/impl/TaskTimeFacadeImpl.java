package com.treefinance.saas.taskcenter.facade.impl;

import com.treefinance.saas.taskcenter.biz.service.TaskTimeService;
import com.treefinance.saas.taskcenter.facade.result.common.TaskResult;
import com.treefinance.saas.taskcenter.facade.service.TaskTimeFacade;
import com.treefinance.saas.taskcenter.service.TaskAttributeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author haojiahong
 * @date 2018/9/27
 */
@Component("taskTimeFacade")
public class TaskTimeFacadeImpl implements TaskTimeFacade {

    @Autowired
    private TaskTimeService taskTimeService;
    @Autowired
    private TaskAttributeService taskAttributeService;

    @Override
    public TaskResult<Void> updateLoginTime(Long taskId, Date date) {
        if (taskId != null && date != null) {
            taskAttributeService.saveLoginTime(taskId, date);
        }

        return TaskResult.wrapSuccessfulResult(null);
    }

    @Override
    public TaskResult<Date> getLoginTime(Long taskId) {
        Date loginTime = taskAttributeService.queryLoginTime(taskId);
        return TaskResult.wrapSuccessfulResult(loginTime);
    }

    @Override
    public TaskResult<Void> handleTaskTimeout(Long taskId) {
        taskTimeService.handleTaskTimeout(taskId);
        return TaskResult.wrapSuccessfulResult(null);
    }

    @Override
    public TaskResult<Void> handleTaskAliveTimeout(Long taskId, Date startTime) {
        taskTimeService.handleTaskAliveTimeout(taskId, startTime);
        return TaskResult.wrapSuccessfulResult(null);
    }
}
