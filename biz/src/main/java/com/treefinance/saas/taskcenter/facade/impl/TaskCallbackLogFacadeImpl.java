package com.treefinance.saas.taskcenter.facade.impl;

import com.google.common.collect.Lists;
import com.treefinance.saas.taskcenter.biz.service.TaskCallbackLogService;
import com.treefinance.saas.taskcenter.context.component.AbstractFacade;
import com.treefinance.saas.taskcenter.dao.entity.TaskCallbackLog;
import com.treefinance.saas.taskcenter.dao.param.TaskCallbackLogQuery;
import com.treefinance.saas.taskcenter.facade.request.TaskCallbackLogPageRequest;
import com.treefinance.saas.taskcenter.facade.request.TaskCallbackLogRequest;
import com.treefinance.saas.taskcenter.facade.result.TaskCallbackLogRO;
import com.treefinance.saas.taskcenter.facade.result.common.TaskPagingResult;
import com.treefinance.saas.taskcenter.facade.result.common.TaskResult;
import com.treefinance.saas.taskcenter.facade.service.TaskBuryPointLogFacade;
import com.treefinance.saas.taskcenter.facade.service.TaskCallbackLogFacade;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * @author guoguoyun
 * @date 2018/9/18上午10:28
 */
@Component("taskCallbackLogFacade")
public class TaskCallbackLogFacadeImpl extends AbstractFacade implements TaskCallbackLogFacade {
    private static final Logger logger = LoggerFactory.getLogger(TaskBuryPointLogFacade.class);

    @Autowired
    private TaskCallbackLogService taskCallbackLogService;

    @Override
    public TaskResult<List<TaskCallbackLogRO>> queryTaskCallbackLog(TaskCallbackLogRequest request) {
        logger.info("查询任务回调日志信息，传入的请求参数为{}", request);

        TaskCallbackLogQuery query = new TaskCallbackLogQuery();
        query.setId(request.getId());

        List<Long> taskIds = request.getTaskIdList();
        if (CollectionUtils.isEmpty(taskIds) && request.getTaskId() != null) {
            taskIds = Collections.singletonList(request.getTaskId());
        }
        query.setTaskIds(taskIds);
        if (request.getConfigId() != null) {
            query.setConfigIds(Collections.singletonList(request.getConfigId()));
        }
        query.setType(request.getType());
        query.setUrl(request.getUrl());
        query.setRequestParam(request.getRequestParam());
        query.setResponseData(request.getResponseData());
        query.setHttpCode(request.getHttpCode());
        query.setConsumeTime(request.getConsumeTime());
        query.setCallbackCode(request.getCallbackCode());
        query.setCallbackMsg(request.getCallbackMsg());
        query.setFailureReason(request.getFailureReason());

        List<TaskCallbackLog> list = taskCallbackLogService.queryTaskCallbackLogs(query);
        List<TaskCallbackLogRO> attributeROList = convert(list, TaskCallbackLogRO.class);

        return TaskResult.wrapSuccessfulResult(attributeROList);
    }

    @Override
    public TaskResult<List<TaskCallbackLogRO>> queryTaskCallbackLog(List<Long> taskIdList) {
        List<TaskCallbackLog> list = taskCallbackLogService.listTaskCallbackLogsInTaskIds(taskIdList);

        List<TaskCallbackLogRO> taskCallbackLogROES = convert(list, TaskCallbackLogRO.class);

        return TaskResult.wrapSuccessfulResult(taskCallbackLogROES);
    }

    @Override
    public TaskPagingResult<TaskCallbackLogRO> queryTaskCallbackLogPage(TaskCallbackLogPageRequest request) {
        List<Long> taskIds = request.getTaskIdList();
        long count = taskCallbackLogService.countTaskCallbackLogsInTaskIds(taskIds);

        if (count <= 0) {
            return TaskPagingResult.wrapSuccessfulResult(null, 0);
        }

        List<TaskCallbackLog> taskCallbackLogList = taskCallbackLogService.listTaskCallbackLogsInTaskIdsWithRowBounds(taskIds, request.getOffset(), request.getPageSize());

        List<TaskCallbackLogRO> taskCallbackLogROES = convert(taskCallbackLogList, TaskCallbackLogRO.class);

        return TaskPagingResult.wrapSuccessfulResult(taskCallbackLogROES, (int)count);
    }

    @Override
    public TaskResult<List<TaskCallbackLogRO>> getTaskCallbackLogs(Long taskId, List<Long> configIds) {
        List<TaskCallbackLog> taskCallbackLogList = taskCallbackLogService.queryTaskCallbackLogsByTaskIdAndInConfigIds(taskId, configIds);
        if (CollectionUtils.isEmpty(taskCallbackLogList)) {
            return TaskResult.wrapSuccessfulResult(Lists.newArrayList());
        }
        List<TaskCallbackLogRO> taskCallbackLogROList = convert(taskCallbackLogList, TaskCallbackLogRO.class);
        return TaskResult.wrapSuccessfulResult(taskCallbackLogROList);
    }

}
