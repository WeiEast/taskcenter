package com.treefinance.saas.taskcenter.facade.impl;

import com.treefinance.saas.taskcenter.exception.BusinessCheckFailException;
import com.treefinance.saas.taskcenter.facade.result.common.TaskResult;
import com.treefinance.saas.taskcenter.facade.service.TaskAliveFacade;
import com.treefinance.saas.taskcenter.service.TaskLifecycleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author haojiahong
 * @date 2018/9/21
 */
@Component("taskAliveFacade")
public class TaskAliveFacadeImpl implements TaskAliveFacade {

    private static final Logger logger = LoggerFactory.getLogger(TaskAliveFacade.class);

    @Autowired
    private TaskLifecycleService taskLifecycleService;

    @Override
    public TaskResult<Void> updateTaskActiveTime(Long taskId) {
        if (taskId == null) {
            throw new BusinessCheckFailException("-1", "任务id不能为空");
        }

        Date date = new Date();

        taskLifecycleService.updateAliveTime(taskId, date);
        
        return TaskResult.wrapSuccessfulResult(null);
    }

    @Override
    public TaskResult<String> getTaskAliveTime(Long taskId) {
        if (taskId == null) {
            throw new BusinessCheckFailException("-1", "任务id不能为空");
        }
        String result = taskLifecycleService.queryAliveTime(taskId);
        return TaskResult.wrapSuccessfulResult(result);
    }

}
