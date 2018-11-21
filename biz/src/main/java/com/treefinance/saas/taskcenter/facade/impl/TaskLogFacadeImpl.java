package com.treefinance.saas.taskcenter.facade.impl;

import com.google.common.collect.Lists;
import com.treefinance.saas.taskcenter.biz.service.TaskLogService;
import com.treefinance.saas.taskcenter.common.util.DataConverterUtils;
import com.treefinance.saas.taskcenter.dao.entity.TaskLog;
import com.treefinance.saas.taskcenter.facade.request.TaskLogRequest;
import com.treefinance.saas.taskcenter.facade.result.TaskLogRO;
import com.treefinance.saas.taskcenter.facade.result.common.TaskResult;
import com.treefinance.saas.taskcenter.facade.service.TaskLogFacade;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author:guoguoyun
 * @date:Created in 2018/9/18上午10:26
 */
@Component("taskLogFacade")
public class TaskLogFacadeImpl implements TaskLogFacade {

    private static final Logger logger = LoggerFactory.getLogger(TaskLogFacade.class);

    @Autowired
    private TaskLogService taskLogService;


    @Override
    public TaskResult<List<TaskLogRO>> queryTaskLog(TaskLogRequest taskLogRequest) {
        logger.info("查询任务日志信息，传入的请求参数为{}", taskLogRequest.toString());
        Long id = taskLogRequest.getId();
        Long taskId = taskLogRequest.getTaskId();
        List<Long> taskIds = taskLogRequest.getTaskIdList();
        if (CollectionUtils.isEmpty(taskIds) && taskId != null) {
            taskIds = Collections.singletonList(taskId);
        }
        String msg = taskLogRequest.getMsg();
        String stepCode = taskLogRequest.getStepCode();
        String errorMsg = taskLogRequest.getErrorMsg();
        Date occurTime = taskLogRequest.getOccurTime();
        String order = taskLogRequest.getOrderByClause();

        List<TaskLog> list = taskLogService.queryTaskLogs(id, taskIds, msg, stepCode, errorMsg, occurTime, order);

        List<TaskLogRO> attributeROList = DataConverterUtils.convert(list, TaskLogRO.class);

        return TaskResult.wrapSuccessfulResult(attributeROList);

    }


    @Override
    public TaskResult<List<TaskLogRO>> queryTaskLogById(TaskLogRequest taskLogRequest) {
        List<Long> taskIds = taskLogRequest.getTaskIdList();
        List<TaskLog> list = taskLogService.listTaskLogsDescWithOccurTimeInTaskIds(taskIds);

        List<TaskLogRO> taskLogROs = DataConverterUtils.convert(list, TaskLogRO.class);

        return TaskResult.wrapSuccessfulResult(taskLogROs);
    }

    @Override
    public TaskResult<TaskLogRO> queryLastestErrorLog(Long taskId) {
        return queryLastErrorLog(taskId);
    }

    @Override
    public TaskResult<TaskLogRO> queryLastErrorLog(Long taskId) {
        TaskLog taskLog = taskLogService.queryLastErrorLog(taskId);
        if (taskLog != null) {
            TaskLogRO taskLogRO = DataConverterUtils.convert(taskLog, TaskLogRO.class);
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
        List<TaskLogRO> taskLogROList = DataConverterUtils.convert(taskLogList, TaskLogRO.class);
        return TaskResult.wrapSuccessfulResult(taskLogROList);
    }

}
