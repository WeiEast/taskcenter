package com.treefinance.saas.taskcenter.biz.facade.impl;

import com.treefinance.saas.taskcenter.biz.service.TaskBuryPointLogService;
import com.treefinance.saas.taskcenter.biz.utils.DataConverterUtils;
import com.treefinance.saas.taskcenter.dao.entity.TaskBuryPointLog;
import com.treefinance.saas.taskcenter.dao.entity.TaskBuryPointLogCriteria;
import com.treefinance.saas.taskcenter.dao.mapper.TaskBuryPointLogMapper;
import com.treefinance.saas.taskcenter.facade.request.TaskBuryPointLogRequest;
import com.treefinance.saas.taskcenter.facade.result.TaskBuryPointLogRO;
import com.treefinance.saas.taskcenter.facade.result.common.TaskResult;
import com.treefinance.saas.taskcenter.facade.service.TaskBuryPointLogFacade;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author:guoguoyun
 * @date:Created in 2018/9/18上午10:27
 */
@Service("taskBuryPointLogFacade")
public class TaskBuryPointLogFacadeImpl implements TaskBuryPointLogFacade {
    private static final Logger logger = LoggerFactory.getLogger(TaskBuryPointLogFacade.class);


    @Autowired
    private TaskBuryPointLogMapper taskBuryPointLogMapper;
    @Autowired
    private TaskBuryPointLogService taskBuryPointLogService;


    @Override
    public TaskResult<List<TaskBuryPointLogRO>> queryTaskBuryPointLog(TaskBuryPointLogRequest taskBuryPointLogRequest) {
        logger.info("查询任务埋点日志信息，传入的请求参数为{}", taskBuryPointLogRequest.toString());
        TaskBuryPointLogCriteria criteria = new TaskBuryPointLogCriteria();
        if (StringUtils.isNotEmpty(taskBuryPointLogRequest.getOrderByClause())) {
            criteria.setOrderByClause(taskBuryPointLogRequest.getOrderByClause());
        }

        TaskBuryPointLogCriteria.Criteria innerCriteria = criteria.createCriteria();


        if (taskBuryPointLogRequest.getId() != null) {
            innerCriteria.andIdEqualTo(taskBuryPointLogRequest.getId());
        }
        if (StringUtils.isNotEmpty(taskBuryPointLogRequest.getAppId())) {
            innerCriteria.andAppIdEqualTo(taskBuryPointLogRequest.getAppId());
        }
        if (taskBuryPointLogRequest.getTaskId() != null) {
            innerCriteria.andTaskIdEqualTo(taskBuryPointLogRequest.getTaskId());
        }
        if (StringUtils.isNotEmpty(taskBuryPointLogRequest.getCode())) {
            innerCriteria.andCodeEqualTo(taskBuryPointLogRequest.getCode());
        }


        List<TaskBuryPointLog> list = taskBuryPointLogMapper.selectByExample(criteria);
        List<TaskBuryPointLogRO> attributeROList = DataConverterUtils.convert(list, TaskBuryPointLogRO.class);

        return TaskResult.wrapSuccessfulResult(attributeROList);

    }

    @Override
    public TaskResult<List<TaskBuryPointLogRO>> queryTaskBuryPointLogById(TaskBuryPointLogRequest taskBuryPointLogRequest) {

        TaskBuryPointLogCriteria taskBuryPointLogCriteria = new TaskBuryPointLogCriteria();
        taskBuryPointLogCriteria.createCriteria().andTaskIdEqualTo(taskBuryPointLogRequest.getTaskId());
        taskBuryPointLogCriteria.setOrderByClause("createTime desc, Id desc");
        List<TaskBuryPointLog> list = taskBuryPointLogMapper.selectByExample(taskBuryPointLogCriteria);

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
