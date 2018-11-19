package com.treefinance.saas.taskcenter.facade.impl;

import com.google.common.collect.Maps;
import com.treefinance.saas.taskcenter.biz.service.TaskAttributeService;
import com.treefinance.saas.taskcenter.common.util.DataConverterUtils;
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
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author:guoguoyun
 * @date:Created in 2018/9/18上午11:02
 */
@Component("taskAttributeFacade")
public class TaskAttributeFacadeImpl implements TaskAttributeFacade {

    private static final Logger logger = LoggerFactory.getLogger(TaskAttributeFacade.class);


    @Autowired
    private TaskAttributeMapper taskAttributeMapper;
    @Autowired
    private TaskAttributeService taskAttributeService;

    @Override
    public TaskResult<List<TaskAttributeRO>> queryTaskAttribute(TaskAttributeRequest taskAttributeRequest) {
        logger.info("查询任务变量信息，传入的请求参数为{}", taskAttributeRequest.toString());
        TaskAttributeCriteria criteria = new TaskAttributeCriteria();
        TaskAttributeCriteria.Criteria innerCriteria = criteria.createCriteria();
        if (taskAttributeRequest.getId() != null) {
            innerCriteria.andIdEqualTo(taskAttributeRequest.getId());
        }
        if (StringUtils.isNotEmpty(taskAttributeRequest.getName())) {
            innerCriteria.andNameEqualTo(taskAttributeRequest.getName());
        }
        if (taskAttributeRequest.getTaskIds() != null) {
            innerCriteria.andTaskIdIn(taskAttributeRequest.getTaskIds());
        }
        if (StringUtils.isNotEmpty(taskAttributeRequest.getValue())) {
            innerCriteria.andValueEqualTo(taskAttributeRequest.getValue());
        }


        List<TaskAttribute> list = taskAttributeMapper.selectByExample(criteria);
        List<TaskAttributeRO> attributeROList = DataConverterUtils.convert(list, TaskAttributeRO.class);

        return TaskResult.wrapSuccessfulResult(attributeROList);

    }


    @Override
    public TaskResult<List<TaskAttributeRO>> queryTaskAttributeByTaskId(TaskAttributeRequest taskAttributeRequest) {

        TaskAttributeCriteria taskAttributeCriteria = new TaskAttributeCriteria();
        taskAttributeCriteria.createCriteria().andTaskIdIn(taskAttributeRequest.getTaskIds()).andNameEqualTo(taskAttributeRequest.getName());
        List<TaskAttribute> list = taskAttributeMapper.selectByExample(taskAttributeCriteria);

        List<TaskAttributeRO> taskAttributeROS = DataConverterUtils.convert(list, TaskAttributeRO.class);

        return TaskResult.wrapSuccessfulResult(taskAttributeROS);
    }

    @Override
    public TaskResult<Map<String, TaskAttributeRO>> findByNames(Long taskId, boolean decrypt, String... names) {
        Map<String, TaskAttribute> map = taskAttributeService.findByNames(taskId, decrypt, names);
        Map<String, TaskAttributeRO> result = Maps.newHashMap();
        for (Map.Entry<String, TaskAttribute> taskAttributeEntry : map.entrySet()) {
            TaskAttributeRO taskAttributeRO = DataConverterUtils.convert(taskAttributeEntry.getValue(), TaskAttributeRO.class);
            result.put(taskAttributeEntry.getKey(), taskAttributeRO);
        }
        return TaskResult.wrapSuccessfulResult(result);
    }

    @Override
    public TaskResult<Long> insert(Long taskId, String name, String value) {
        Long id = taskAttributeService.insert(taskId, name, value);
        return TaskResult.wrapSuccessfulResult(id);
    }

    @Override
    public TaskResult<Void> insertOrUpdateSelective(Long taskId, String name, String value) {
        taskAttributeService.insertOrUpdateSelective(taskId, name, value);
        return TaskResult.wrapSuccessfulResult(null);
    }

    @Override
    public TaskResult<TaskAttributeRO> findByName(Long taskId, String name, boolean decrypt) {
        TaskAttribute taskAttribute = taskAttributeService.findByName(taskId, name, decrypt);
        if (taskAttribute == null) {
            return TaskResult.wrapSuccessfulResult(null);
        }
        TaskAttributeRO attributeRO = DataConverterUtils.convert(taskAttribute, TaskAttributeRO.class);
        return TaskResult.wrapSuccessfulResult(attributeRO);
    }

    @Override
    public TaskResult<TaskAttributeRO> findByNameAndValue(String name, String value, boolean encrypt) {
        TaskAttribute taskAttribute = taskAttributeService.findByNameAndValue(name, value, encrypt);
        TaskAttributeRO taskAttributeRO = DataConverterUtils.convert(taskAttribute, TaskAttributeRO.class);
        return TaskResult.wrapSuccessfulResult(taskAttributeRO);
    }

    @Override
    public TaskResult<List<TaskAttributeRO>> findByTaskId(Long taskId) {
        List<TaskAttribute> taskAttributeList = taskAttributeService.findByTaskId(taskId);
        List<TaskAttributeRO> taskAttributeROList = DataConverterUtils.convert(taskAttributeList, TaskAttributeRO.class);
        return TaskResult.wrapSuccessfulResult(taskAttributeROList);
    }
}
