package com.treefinance.saas.taskcenter.biz.facade.impl;

import com.treefinance.saas.knife.result.Results;
import com.treefinance.saas.knife.result.SaasResult;
import com.treefinance.saas.taskcenter.biz.utils.DataConverterUtils;
import com.treefinance.saas.taskcenter.dao.entity.TaskBuryPointLog;
import com.treefinance.saas.taskcenter.dao.entity.TaskBuryPointLogCriteria;
import com.treefinance.saas.taskcenter.dao.entity.TaskCallbackLog;
import com.treefinance.saas.taskcenter.dao.entity.TaskCallbackLogCriteria;
import com.treefinance.saas.taskcenter.dao.mapper.TaskBuryPointLogMapper;
import com.treefinance.saas.taskcenter.dao.mapper.TaskCallbackLogMapper;
import com.treefinance.saas.taskcenter.facade.request.TaskBuryPointLogRequest;
import com.treefinance.saas.taskcenter.facade.request.TaskCallbackLogPageRequest;
import com.treefinance.saas.taskcenter.facade.request.TaskCallbackLogRequest;
import com.treefinance.saas.taskcenter.facade.result.TaskBuryPointLogRO;
import com.treefinance.saas.taskcenter.facade.result.TaskCallbackLogRO;
import com.treefinance.saas.taskcenter.facade.result.common.TaskPagingResult;
import com.treefinance.saas.taskcenter.facade.result.common.TaskResult;
import com.treefinance.saas.taskcenter.facade.service.TaskBuryPointLogFacade;
import com.treefinance.saas.taskcenter.facade.service.TaskCallbackLogFacade;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author:guoguoyun
 * @date:Created in 2018/9/18上午10:28
 */
@Service("taskCallbackLogFacade")
public class TaskCallbackLogFacadeImpl implements TaskCallbackLogFacade {
    private static final Logger logger = LoggerFactory.getLogger(TaskBuryPointLogFacade.class);


    @Autowired
    private TaskCallbackLogMapper taskCallbackLogMapper;


    @Override
    public TaskResult<List<TaskCallbackLogRO>> queryTaskCallbackLog(TaskCallbackLogRequest taskCallbackLogRequest) {
        logger.info("查询任务回调日志信息，传入的请求参数为{}", taskCallbackLogRequest.toString());
        TaskCallbackLogCriteria criteria = new TaskCallbackLogCriteria();
        TaskCallbackLogCriteria.Criteria innerCriteria = criteria.createCriteria();
        if (taskCallbackLogRequest.getId() == null) {
            innerCriteria.andIdEqualTo(taskCallbackLogRequest.getId());
        }
        if (taskCallbackLogRequest.getConfigId() == null) {
            innerCriteria.andConfigIdEqualTo(taskCallbackLogRequest.getConfigId());
        }
        if (taskCallbackLogRequest.getHttpCode() == null) {
            innerCriteria.andHttpCodeEqualTo(taskCallbackLogRequest.getHttpCode());
        }
        if (taskCallbackLogRequest.getFailureReason() == null) {
            innerCriteria.andFailureReasonEqualTo(taskCallbackLogRequest.getFailureReason());
        }
        if (taskCallbackLogRequest.getType() == null) {
            innerCriteria.andTypeEqualTo(taskCallbackLogRequest.getType());
        }
        if (taskCallbackLogRequest.getConsumeTime() == null) {
            innerCriteria.andConsumeTimeEqualTo(taskCallbackLogRequest.getConsumeTime());
        }
        if (StringUtils.isEmpty(taskCallbackLogRequest.getCallbackCode())) {
            innerCriteria.andCallbackCodeEqualTo(taskCallbackLogRequest.getCallbackCode());
        }
        if (taskCallbackLogRequest.getTaskId() == null) {
            innerCriteria.andTaskIdEqualTo(taskCallbackLogRequest.getTaskId());
        }
        if (taskCallbackLogRequest.getTaskIdList() == null) {
            innerCriteria.andTaskIdIn(taskCallbackLogRequest.getTaskIdList());
        }
        if (StringUtils.isEmpty(taskCallbackLogRequest.getCallbackMsg())) {
            innerCriteria.andCallbackMsgEqualTo(taskCallbackLogRequest.getCallbackMsg());
        }
        if (StringUtils.isEmpty(taskCallbackLogRequest.getRequestParam())) {
            innerCriteria.andRequestParamEqualTo(taskCallbackLogRequest.getRequestParam());
        }
        if (StringUtils.isEmpty(taskCallbackLogRequest.getResponseData())) {
            innerCriteria.andResponseDataEqualTo(taskCallbackLogRequest.getResponseData());
        }
        if (StringUtils.isEmpty(taskCallbackLogRequest.getUrl())) {
            innerCriteria.andUrlEqualTo(taskCallbackLogRequest.getUrl());
        }

        List<TaskCallbackLog> list = taskCallbackLogMapper.selectByExample(criteria);
        List<TaskCallbackLogRO> attributeROList = DataConverterUtils.convert(list, TaskCallbackLogRO.class);

        return TaskResult.wrapSuccessfulResult(attributeROList);
    }


    @Override
    public TaskResult<List<TaskCallbackLogRO>> queryTaskCallbackLog(List<Long> taskIdList) {

        TaskCallbackLogCriteria taskCallbackLogCriteria = new TaskCallbackLogCriteria();
        taskCallbackLogCriteria.createCriteria().andTaskIdIn(taskIdList);
        List<TaskCallbackLog> list = taskCallbackLogMapper.selectByExample(taskCallbackLogCriteria);

        List<TaskCallbackLogRO> taskCallbackLogROES = DataConverterUtils.convert(list, TaskCallbackLogRO.class);

        return TaskResult.wrapSuccessfulResult(taskCallbackLogROES);
    }

    @Override
    public TaskPagingResult<TaskCallbackLogRO> queryTaskCallbackLogPage(TaskCallbackLogPageRequest request) {


        TaskCallbackLogCriteria taskCallbackLogCriteria = new TaskCallbackLogCriteria();
        taskCallbackLogCriteria.setOffset(request.getOffset());
        taskCallbackLogCriteria.setLimit(request.getPageSize());
        taskCallbackLogCriteria.createCriteria().andTaskIdIn(request.getTaskIdList());
        Long count = taskCallbackLogMapper.countByExample(taskCallbackLogCriteria);
        if (count <= 0) {
            return TaskPagingResult.wrapSuccessfulResult(null, 0);
        }
        List<TaskCallbackLog> taskCallbackLogList = taskCallbackLogMapper.selectPaginationByExample
                (taskCallbackLogCriteria);

        List<TaskCallbackLogRO> taskCallbackLogROES = DataConverterUtils.convert(taskCallbackLogList, TaskCallbackLogRO.class);


        return TaskPagingResult.wrapSuccessfulResult(taskCallbackLogROES, count.intValue());
    }
}
