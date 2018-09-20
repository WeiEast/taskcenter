package com.treefinance.saas.taskcenter.biz.facade.impl;

import com.treefinance.saas.knife.result.Results;
import com.treefinance.saas.knife.result.SaasResult;
import com.treefinance.saas.taskcenter.biz.utils.DataConverterUtils;
import com.treefinance.saas.taskcenter.dao.entity.TaskBuryPointLog;
import com.treefinance.saas.taskcenter.dao.entity.TaskBuryPointLogCriteria;
import com.treefinance.saas.taskcenter.dao.entity.TaskLog;
import com.treefinance.saas.taskcenter.dao.entity.TaskLogCriteria;
import com.treefinance.saas.taskcenter.dao.mapper.TaskBuryPointLogMapper;
import com.treefinance.saas.taskcenter.dao.mapper.TaskLogMapper;
import com.treefinance.saas.taskcenter.facade.request.TaskBuryPointLogRequest;
import com.treefinance.saas.taskcenter.facade.request.TaskLogRequest;
import com.treefinance.saas.taskcenter.facade.result.TaskBuryPointLogRO;
import com.treefinance.saas.taskcenter.facade.result.TaskLogRO;
import com.treefinance.saas.taskcenter.facade.result.common.TaskResult;
import com.treefinance.saas.taskcenter.facade.service.TaskBuryPointLogFacade;
import com.treefinance.saas.taskcenter.facade.service.TaskLogFacade;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author:guoguoyun
 * @date:Created in 2018/9/18上午10:26
 */
@Service("taskLogFacade")
public class TaskLogFacadeImpl implements TaskLogFacade {

    private static final Logger logger = LoggerFactory.getLogger(TaskLogFacade.class);


    @Autowired
    private TaskLogMapper taskLogMapper;


    @Override
    public TaskResult<List<TaskLogRO>> queryTaskLog(TaskLogRequest taskLogRequest) {
        logger.info("查询任务日志信息，传入的请求参数为{}", taskLogRequest.toString());
        TaskLogCriteria criteria = new TaskLogCriteria();
        if (StringUtils.isNotEmpty(taskLogRequest.getOrderByClause())) {
            criteria.setOrderByClause(taskLogRequest.getOrderByClause());
        }

        TaskLogCriteria.Criteria innerCriteria = criteria.createCriteria();


        if (taskLogRequest.getId() == null) {
            innerCriteria.andIdEqualTo(taskLogRequest.getId());
        }
        if (StringUtils.isEmpty(taskLogRequest.getErrorMsg())) {
            innerCriteria.andErrorMsgEqualTo(taskLogRequest.getErrorMsg());
        }
        if (taskLogRequest.getTaskId() == null) {
            innerCriteria.andTaskIdEqualTo(taskLogRequest.getTaskId());
        }
        if (taskLogRequest.getTaskIdList() == null) {
            innerCriteria.andTaskIdIn(taskLogRequest.getTaskIdList());
        }
        if (StringUtils.isEmpty(taskLogRequest.getMsg())) {
            innerCriteria.andMsgEqualTo(taskLogRequest.getMsg());
        }
        if (StringUtils.isEmpty(taskLogRequest.getStepCode())) {
            innerCriteria.andStepCodeEqualTo(taskLogRequest.getStepCode());
        }
        if (taskLogRequest.getOccurTime() == null) {
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
}
