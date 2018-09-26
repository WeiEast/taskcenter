package com.treefinance.saas.taskcenter.biz.facade.impl;

import com.treefinance.saas.taskcenter.biz.service.TaskAliveService;
import com.treefinance.saas.taskcenter.common.exception.BusinessCheckFailException;
import com.treefinance.saas.taskcenter.facade.result.common.TaskResult;
import com.treefinance.saas.taskcenter.facade.service.TaskAliveFacade;
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
    private TaskAliveService taskAliveService;


    /**
     * 更新任务最近活跃时间
     * 可能存在多个请求同时更新活跃时间,未获得锁的请求可过滤掉
     *
     * @param taskId
     * @return
     */
    @Override
    public TaskResult<Void> updateTaskActiveTime(Long taskId, Date date) {
        if (taskId == null) {
            throw new BusinessCheckFailException("-1", "任务id不能为空");
        }
        if (date == null) {
            throw new BusinessCheckFailException("-1", "任务活跃时间不能为空");
        }
        taskAliveService.updateTaskActiveTime(taskId, date);
        return TaskResult.wrapSuccessfulResult(null);

    }

    @Override
    public TaskResult<Void> updateTaskActiveTime(Long taskId) {
        return this.updateTaskActiveTime(taskId, new Date());
    }

    /**
     * 获取任务最近活跃时间
     *
     * @param taskId
     * @return
     */
    @Override
    public TaskResult<String> getTaskAliveTime(Long taskId) {
        if (taskId == null) {
            throw new BusinessCheckFailException("-1", "任务id不能为空");
        }
        String result = taskAliveService.getTaskAliveTime(taskId);
        return TaskResult.wrapSuccessfulResult(result);
    }

    @Override
    public TaskResult<Void> deleteTaskAliveTime(Long taskId) {
        if (taskId == null) {
            throw new BusinessCheckFailException("-1", "任务id不能为空");
        }
        taskAliveService.deleteTaskAliveTime(taskId);
        return TaskResult.wrapSuccessfulResult(null);
    }
}
