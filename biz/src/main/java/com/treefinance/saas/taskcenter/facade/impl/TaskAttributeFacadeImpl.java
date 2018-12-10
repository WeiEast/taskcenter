package com.treefinance.saas.taskcenter.facade.impl;

import com.google.common.collect.Maps;
import com.treefinance.saas.taskcenter.biz.service.TaskAttributeService;
import com.treefinance.saas.taskcenter.context.component.AbstractFacade;
import com.treefinance.saas.taskcenter.dao.domain.TaskAttributeQuery;
import com.treefinance.saas.taskcenter.dao.entity.TaskAttribute;
import com.treefinance.saas.taskcenter.facade.request.TaskAttributeRequest;
import com.treefinance.saas.taskcenter.facade.result.TaskAttributeRO;
import com.treefinance.saas.taskcenter.facade.result.common.TaskResult;
import com.treefinance.saas.taskcenter.facade.service.TaskAttributeFacade;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author guoguoyun
 * @date 2018/9/18上午11:02
 */
@Component("taskAttributeFacade")
public class TaskAttributeFacadeImpl extends AbstractFacade implements TaskAttributeFacade {

    private static final Logger logger = LoggerFactory.getLogger(TaskAttributeFacade.class);

    @Autowired
    private TaskAttributeService taskAttributeService;

    @Override
    public TaskResult<List<TaskAttributeRO>> queryTaskAttribute(TaskAttributeRequest request) {
        logger.info("查询任务变量信息，传入的请求参数为{}", request.toString());
        TaskAttributeQuery query = new TaskAttributeQuery();
        query.setId(request.getId());
        query.setTaskIds(request.getTaskIds());
        query.setName(request.getName());
        query.setValue(request.getValue());

        List<TaskAttribute> list = taskAttributeService.queryTaskAttributes(query);

        List<TaskAttributeRO> attributeROList = convert(list, TaskAttributeRO.class);

        return TaskResult.wrapSuccessfulResult(attributeROList);
    }


    @Override
    public TaskResult<List<TaskAttributeRO>> queryTaskAttributeByTaskId(TaskAttributeRequest request) {
        List<TaskAttribute> list = taskAttributeService.listTaskAttributesByNameAndInTaskIds(request.getName(), request.getTaskIds());

        List<TaskAttributeRO> taskAttributeROS = convert(list, TaskAttributeRO.class);

        return TaskResult.wrapSuccessfulResult(taskAttributeROS);
    }

    @Override
    public TaskResult<Map<String, TaskAttributeRO>> findByNames(Long taskId, boolean decrypt, String... names) {
        Map<String, TaskAttributeRO> result;

        List<TaskAttribute> attributes = taskAttributeService.listTaskAttributesByTaskIdAndInNames(taskId, names, decrypt);
        if (CollectionUtils.isNotEmpty(attributes)) {
            result = Maps.newHashMap();
            for (TaskAttribute attribute : attributes) {
                TaskAttributeRO taskAttributeRO = convert(attribute, TaskAttributeRO.class);
                result.put(attribute.getName(), taskAttributeRO);
            }
        } else {
            result = Collections.emptyMap();
        }

        return TaskResult.wrapSuccessfulResult(result);
    }

    @Override
    public TaskResult<Long> insert(Long taskId, String name, String value) {
        Long id = taskAttributeService.insert(taskId, name, value, false);
        return TaskResult.wrapSuccessfulResult(id);
    }

    @Override
    public TaskResult<Long> insert(Long taskId, String name, String value, boolean sensitive) {
        Long id = taskAttributeService.insert(taskId, name, value, sensitive);
        return TaskResult.wrapSuccessfulResult(id);
    }

    @Override
    public TaskResult<Void> insertOrUpdateSelective(Long taskId, String name, String value) {
        taskAttributeService.insertOrUpdate(taskId, name, value);
        return TaskResult.wrapSuccessfulResult(null);
    }

    @Override
    public TaskResult<TaskAttributeRO> findByName(Long taskId, String name, boolean decrypt) {
        TaskAttribute taskAttribute = taskAttributeService.findByName(taskId, name, decrypt);
        if (taskAttribute == null) {
            return TaskResult.wrapSuccessfulResult(null);
        }
        TaskAttributeRO attributeRO = convert(taskAttribute, TaskAttributeRO.class);
        return TaskResult.wrapSuccessfulResult(attributeRO);
    }

    @Override
    public TaskResult<TaskAttributeRO> findByNameAndValue(String name, String value, boolean encrypt) {
        TaskAttribute taskAttribute = taskAttributeService.findByNameAndValue(name, value, encrypt);
        TaskAttributeRO taskAttributeRO = convert(taskAttribute, TaskAttributeRO.class);
        return TaskResult.wrapSuccessfulResult(taskAttributeRO);
    }

    @Override
    public TaskResult<List<TaskAttributeRO>> findByTaskId(Long taskId) {
        List<TaskAttribute> taskAttributeList = taskAttributeService.findByTaskId(taskId);
        List<TaskAttributeRO> taskAttributeROList = convert(taskAttributeList, TaskAttributeRO.class);
        return TaskResult.wrapSuccessfulResult(taskAttributeROList);
    }
}
