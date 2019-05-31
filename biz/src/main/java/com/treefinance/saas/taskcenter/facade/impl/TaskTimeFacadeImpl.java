package com.treefinance.saas.taskcenter.facade.impl;

import com.treefinance.saas.taskcenter.biz.schedule.detector.TaskAliveTimeDetector;
import com.treefinance.saas.taskcenter.biz.schedule.detector.TaskTimeoutDetector;
import com.treefinance.saas.taskcenter.facade.response.TaskResponse;
import com.treefinance.saas.taskcenter.facade.result.common.TaskResult;
import com.treefinance.saas.taskcenter.facade.service.TaskTimeFacade;
import com.treefinance.saas.taskcenter.service.TaskAttributeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author haojiahong
 * @date 2018/9/27
 */
@Component("taskTimeFacade")
public class TaskTimeFacadeImpl implements TaskTimeFacade {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskTimeFacadeImpl.class);
    @Autowired
    private TaskAttributeService taskAttributeService;
    @Autowired
    private ThreadPoolTaskExecutor threadPoolExecutor;
    @Autowired
    private TaskTimeoutDetector taskTimeoutDetector;
    @Autowired
    private TaskAliveTimeDetector taskAliveTimeDetector;

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
        LOGGER.info("异步检测超时任务并处理 >>> taskId: {}", taskId);
        threadPoolExecutor.execute(() -> {
            try {
                taskTimeoutDetector.detect(taskId);
            } catch (InterruptedException e) {
                LOGGER.error("Task's timeout detector interrupted!", e);
            }
        });
        return TaskResult.wrapSuccessfulResult(null);
    }

    @Override
    public TaskResult<Void> handleTaskAliveTimeout(Long taskId, Date startTime) {
        LOGGER.info("任务活跃超时异步处理:taskId={},startTime={}", taskId, startTime);
        threadPoolExecutor.execute(() -> {
            try {
                taskAliveTimeDetector.detect(taskId, startTime);
            } catch (InterruptedException e) {
                LOGGER.error("Task's timeout detector interrupted!", e);
            }
        });
        return TaskResult.wrapSuccessfulResult(null);
    }

    @Override
    public TaskResponse<Void> processTaskIfTimeout(Long taskId) {
        LOGGER.info("异步检测超时任务并处理 >>> taskId: {}", taskId);
        threadPoolExecutor.execute(() -> {
            try {
                taskTimeoutDetector.detect(taskId);
            } catch (InterruptedException e) {
                LOGGER.error("Task's timeout detector interrupted!", e);
            }
        });
        return TaskResponse.success(null);
    }
}
