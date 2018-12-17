package com.treefinance.saas.taskcenter.facade.impl;

import com.google.common.collect.Lists;
import com.treefinance.saas.taskcenter.biz.service.TaskLogService;
import com.treefinance.saas.taskcenter.context.component.AbstractFacade;
import com.treefinance.saas.taskcenter.dao.entity.TaskLog;
import com.treefinance.saas.taskcenter.facade.request.TaskLogRequest;
import com.treefinance.saas.taskcenter.facade.result.TaskLogRO;
import com.treefinance.saas.taskcenter.facade.result.common.TaskResult;
import com.treefinance.saas.taskcenter.facade.service.TaskLogFacade;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author guoguoyun
 * @date 2018/9/18上午10:26
 */
@Component("taskLogFacade")
public class TaskLogFacadeImpl extends AbstractFacade implements TaskLogFacade {

    @Autowired
    private TaskLogService taskLogService;


    @Override
    public TaskResult<List<TaskLogRO>> queryTaskLog(TaskLogRequest request) {
        logger.info("查询任务日志信息，传入的请求参数为{}", request);
        Long id = request.getId();
        Long taskId = request.getTaskId();
        List<Long> taskIds = request.getTaskIdList();
        if (CollectionUtils.isEmpty(taskIds) && taskId != null) {
            taskIds = Collections.singletonList(taskId);
        }
        String msg = request.getMsg();
        String stepCode = request.getStepCode();
        String errorMsg = request.getErrorMsg();
        Date occurTime = request.getOccurTime();
        String order = request.getOrderByClause();

        List<TaskLog> list = taskLogService.queryTaskLogs(id, taskIds, msg, stepCode, errorMsg, occurTime, order);

        List<TaskLogRO> attributeROList = convert(list, TaskLogRO.class);

        return TaskResult.wrapSuccessfulResult(attributeROList);

    }


    @Override
    public TaskResult<List<TaskLogRO>> queryTaskLogById(TaskLogRequest taskLogRequest) {
        List<Long> taskIds = taskLogRequest.getTaskIdList();
        List<TaskLog> list = taskLogService.listTaskLogsDescWithOccurTimeInTaskIds(taskIds);

        List<TaskLogRO> taskLogROs = convert(list, TaskLogRO.class);

        return TaskResult.wrapSuccessfulResult(taskLogROs);
    }

    @Override
    public TaskResult<TaskLogRO> queryLastErrorLog(Long taskId) {
        TaskLog taskLog = taskLogService.queryLastErrorLog(taskId);
        if (taskLog != null) {
            TaskLogRO taskLogRO = convert(taskLog, TaskLogRO.class);
            return TaskResult.wrapSuccessfulResult(taskLogRO);
        }
        return TaskResult.wrapSuccessfulResult(null);
    }

    @Override
    public TaskResult<Long> insert(Long taskId, String msg, Date processTime, String errorMsg) {
        long id = taskLogService.insertTaskLog(taskId, msg, processTime, errorMsg);
        return TaskResult.wrapSuccessfulResult(id);
    }

    @Override
    public TaskResult<List<TaskLogRO>> queryTaskLog(Long taskId, String msg) {
        List<TaskLog> taskLogList = taskLogService.listTaskLogsByTaskIdAndMsg(taskId, msg);
        if (CollectionUtils.isEmpty(taskLogList)) {
            return TaskResult.wrapSuccessfulResult(Lists.newArrayList());
        }
        List<TaskLogRO> taskLogROList = convert(taskLogList, TaskLogRO.class);
        return TaskResult.wrapSuccessfulResult(taskLogROList);
    }

}
