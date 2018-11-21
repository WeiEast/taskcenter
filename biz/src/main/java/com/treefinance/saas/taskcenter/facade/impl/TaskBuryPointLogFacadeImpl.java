package com.treefinance.saas.taskcenter.facade.impl;

import com.treefinance.saas.taskcenter.biz.service.TaskBuryPointLogService;
import com.treefinance.saas.taskcenter.common.util.DataConverterUtils;
import com.treefinance.saas.taskcenter.dao.entity.TaskBuryPointLog;
import com.treefinance.saas.taskcenter.facade.request.TaskBuryPointLogRequest;
import com.treefinance.saas.taskcenter.facade.result.TaskBuryPointLogRO;
import com.treefinance.saas.taskcenter.facade.result.common.TaskResult;
import com.treefinance.saas.taskcenter.facade.service.TaskBuryPointLogFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author:guoguoyun
 * @date:Created in 2018/9/18上午10:27
 */
@Component("taskBuryPointLogFacade")
public class TaskBuryPointLogFacadeImpl implements TaskBuryPointLogFacade {
    private static final Logger logger = LoggerFactory.getLogger(TaskBuryPointLogFacade.class);


    @Autowired
    private TaskBuryPointLogService taskBuryPointLogService;


    @Override
    public TaskResult<List<TaskBuryPointLogRO>> queryTaskBuryPointLog(TaskBuryPointLogRequest taskBuryPointLogRequest) {
        logger.info("查询任务埋点日志信息，传入的请求参数为{}", taskBuryPointLogRequest.toString());

        Long id = taskBuryPointLogRequest.getId();
        String appId = taskBuryPointLogRequest.getAppId();
        Long taskId = taskBuryPointLogRequest.getTaskId();
        String code = taskBuryPointLogRequest.getCode();
        String order = taskBuryPointLogRequest.getOrderByClause();

        List<TaskBuryPointLog> list = taskBuryPointLogService.queryTaskBuryPointLogs(id, appId, taskId, code, order);

        List<TaskBuryPointLogRO> attributeROList = DataConverterUtils.convert(list, TaskBuryPointLogRO.class);

        return TaskResult.wrapSuccessfulResult(attributeROList);

    }

    @Override
    public TaskResult<List<TaskBuryPointLogRO>> queryTaskBuryPointLogById(TaskBuryPointLogRequest taskBuryPointLogRequest) {
        Long taskId = taskBuryPointLogRequest.getTaskId();
        List<TaskBuryPointLog> list = taskBuryPointLogService.listTaskBuryPointLogsDescWithCreateTimeByTaskId(taskId);

        List<TaskBuryPointLogRO> attributeROList = DataConverterUtils.convert(list, TaskBuryPointLogRO.class);

        return TaskResult.wrapSuccessfulResult(attributeROList);
    }

    @Override
    public TaskResult<Void> pushTaskBuryPointLog(Long taskId, String appId, String code) {
        taskBuryPointLogService.pushTaskBuryPointLog(taskId, appId, code);
        return TaskResult.wrapSuccessfulResult(null);
    }

    @Override
    public TaskResult<Void> logTaskOperatorMaintainUser(Long taskId, String appId, String extra) {
        taskBuryPointLogService.logTaskOperatorMaintainUser(taskId, appId, extra);
        return TaskResult.wrapSuccessfulResult(null);
    }
}
