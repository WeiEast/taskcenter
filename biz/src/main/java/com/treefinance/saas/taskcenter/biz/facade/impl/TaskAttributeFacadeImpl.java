package com.treefinance.saas.taskcenter.biz.facade.impl;

import com.treefinance.saas.knife.result.Results;
import com.treefinance.saas.knife.result.SaasResult;
import com.treefinance.saas.taskcenter.biz.utils.DataConverterUtils;
import com.treefinance.saas.taskcenter.dao.entity.TaskAttribute;
import com.treefinance.saas.taskcenter.dao.entity.TaskAttributeCriteria;
import com.treefinance.saas.taskcenter.dao.mapper.TaskAttributeMapper;
import com.treefinance.saas.taskcenter.facade.request.TaskAttributeRequest;
import com.treefinance.saas.taskcenter.facade.result.TaskAttributeRO;
import com.treefinance.saas.taskcenter.facade.result.common.TaskResult;
import com.treefinance.saas.taskcenter.facade.service.TaskAttributeFacade;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author:guoguoyun
 * @date:Created in 2018/9/18上午11:02
 */
@Service("taskAttributeFacade")
public class TaskAttributeFacadeImpl implements TaskAttributeFacade {

    private static final Logger logger = LoggerFactory.getLogger(TaskAttributeFacade.class);


    @Autowired
    private TaskAttributeMapper taskAttributeMapper;


    public TaskResult<List<TaskAttributeRO>> queryTaskAttribute(TaskAttributeRequest taskAttributeRequest) {
        logger.info("查询任务变量信息，传入的请求参数为{}", taskAttributeRequest.toString());
        TaskAttributeCriteria criteria = new TaskAttributeCriteria();
        TaskAttributeCriteria.Criteria innerCriteria = criteria.createCriteria();
        if (taskAttributeRequest.getId() == null) {
            innerCriteria.andIdEqualTo(taskAttributeRequest.getId());
        }
        if (StringUtils.isEmpty(taskAttributeRequest.getName())) {
            innerCriteria.andNameEqualTo(taskAttributeRequest.getName());
        }
        if (taskAttributeRequest.getTaskId() == null) {
            innerCriteria.andTaskIdEqualTo(taskAttributeRequest.getTaskId());
        }
        if (StringUtils.isEmpty(taskAttributeRequest.getValue())) {
            innerCriteria.andValueEqualTo(taskAttributeRequest.getValue());
        }


        List<TaskAttribute> list = taskAttributeMapper.selectByExample(criteria);
        List<TaskAttributeRO> attributeROList = DataConverterUtils.convert(list, TaskAttributeRO.class);

        return TaskResult.wrapSuccessfulResult(attributeROList);

    }


}
