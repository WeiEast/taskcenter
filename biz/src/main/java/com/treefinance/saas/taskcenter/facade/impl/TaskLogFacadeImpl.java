package com.treefinance.saas.taskcenter.facade.impl;

import com.google.common.collect.Lists;
import com.treefinance.saas.taskcenter.biz.service.TaskLogService;
import com.treefinance.saas.taskcenter.common.util.DataConverterUtils;
import com.treefinance.saas.taskcenter.dao.entity.TaskLog;
import com.treefinance.saas.taskcenter.dao.entity.TaskLogCriteria;
import com.treefinance.saas.taskcenter.dao.mapper.TaskLogMapper;
import com.treefinance.saas.taskcenter.facade.request.TaskLogRequest;
import com.treefinance.saas.taskcenter.facade.result.TaskLogRO;
import com.treefinance.saas.taskcenter.facade.result.common.TaskResult;
import com.treefinance.saas.taskcenter.facade.service.TaskLogFacade;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    private TaskLogMapper taskLogMapper;
    @Autowired
    private TaskLogService taskLogService;


    @Override
    public TaskResult<List<TaskLogRO>> queryTaskLog(TaskLogRequest taskLogRequest) {
        logger.info("查询任务日志信息，传入的请求参数为{}", taskLogRequest.toString());
        TaskLogCriteria criteria = new TaskLogCriteria();
        if (StringUtils.isNotEmpty(taskLogRequest.getOrderByClause())) {
            criteria.setOrderByClause(taskLogRequest.getOrderByClause());
        }

        TaskLogCriteria.Criteria innerCriteria = criteria.createCriteria();


        if (taskLogRequest.getId() != null) {
            innerCriteria.andIdEqualTo(taskLogRequest.getId());
        }
        if (StringUtils.isNotEmpty(taskLogRequest.getErrorMsg())) {
            innerCriteria.andErrorMsgEqualTo(taskLogRequest.getErrorMsg());
        }
        if (taskLogRequest.getTaskId() != null) {
            innerCriteria.andTaskIdEqualTo(taskLogRequest.getTaskId());
        }
        if (taskLogRequest.getTaskIdList() != null) {
            innerCriteria.andTaskIdIn(taskLogRequest.getTaskIdList());
        }
        if (StringUtils.isNotEmpty(taskLogRequest.getMsg())) {
            innerCriteria.andMsgEqualTo(taskLogRequest.getMsg());
        }
        if (StringUtils.isNotEmpty(taskLogRequest.getStepCode())) {
            innerCriteria.andStepCodeEqualTo(taskLogRequest.getStepCode());
        }
        if (taskLogRequest.getOccurTime() != null) {
            innerCriteria.andOccurTimeEqualTo(taskLogRequest.getOccurTime());
        }

        List<TaskLog> list = taskLogMapper.selectByExample(criteria);
        List<TaskLogRO> attributeROList = DataConverterUtils.convert(list, TaskLogRO.class);

        return TaskResult.wrapSuccessfulResult(attributeROList);

    }


    @Override
    public TaskResult<List<TaskLogRO>> queryTaskLogById(TaskLogRequest taskLogRequest) {
        TaskLogCriteria taskLogCriteria = new TaskLogCriteria();
        taskLogCriteria.createCriteria().andTaskIdIn(taskLogRequest.getTaskIdList());
        taskLogCriteria.setOrderByClause("OccurTime desc, Id desc");
        List<TaskLog> list = taskLogMapper.selectByExample(taskLogCriteria);

        List<TaskLogRO> taskLogROs = DataConverterUtils.convert(list, TaskLogRO.class);

        return TaskResult.wrapSuccessfulResult(taskLogROs);
    }

    @Override
    public TaskResult<TaskLogRO> queryLastestErrorLog(Long taskId) {
        TaskLog taskLog = taskLogService.queryLastestErrorLog(taskId);
        if (taskLog == null) {
            return TaskResult.wrapSuccessfulResult(null);
        }
        TaskLogRO taskLogRO = DataConverterUtils.convert(taskLog, TaskLogRO.class);
        return TaskResult.wrapSuccessfulResult(taskLogRO);
    }

    @Override
    public TaskResult<Long> insert(Long taskId, String msg, Date processTime, String errorMsg) {
        long id = taskLogService.insert(taskId, msg, processTime, errorMsg);
        return TaskResult.wrapSuccessfulResult(id);
    }

    @Override
    public TaskResult<List<TaskLogRO>> queryTaskLog(Long taskId, String msg) {
        List<TaskLog> taskLogList = taskLogService.queryTaskLog(taskId, msg);
        if (CollectionUtils.isEmpty(taskLogList)) {
            return TaskResult.wrapSuccessfulResult(Lists.newArrayList());
        }
        List<TaskLogRO> taskLogROList = DataConverterUtils.convert(taskLogList, TaskLogRO.class);
        return TaskResult.wrapSuccessfulResult(taskLogROList);
    }

}
