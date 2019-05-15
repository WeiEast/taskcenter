package com.treefinance.saas.taskcenter.facade.impl;

import com.google.common.collect.Maps;
import com.treefinance.saas.taskcenter.dao.entity.TaskAttribute;
import com.treefinance.saas.taskcenter.dao.param.TaskAttributeQuery;
import com.treefinance.saas.taskcenter.facade.request.AttributeBatchQueryRequest;
import com.treefinance.saas.taskcenter.facade.request.TaskAttributesSaveRequest;
import com.treefinance.saas.taskcenter.facade.request.MultiAttributeQueryRequest;
import com.treefinance.saas.taskcenter.facade.request.SavedTaskAttribute;
import com.treefinance.saas.taskcenter.facade.request.TaskAttributeRequest;
import com.treefinance.saas.taskcenter.facade.response.TaskResponse;
import com.treefinance.saas.taskcenter.facade.result.AttributeDTO;
import com.treefinance.saas.taskcenter.facade.result.SimpleAttributeDTO;
import com.treefinance.saas.taskcenter.facade.result.TaskAttributeRO;
import com.treefinance.saas.taskcenter.facade.result.common.TaskResult;
import com.treefinance.saas.taskcenter.facade.service.TaskAttributeFacade;
import com.treefinance.saas.taskcenter.facade.validate.Preconditions;
import com.treefinance.saas.taskcenter.service.TaskAttributeService;
import com.treefinance.saas.taskcenter.service.param.TaskAttributeSaveObject;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author guoguoyun
 * @date 2018/9/18上午11:02
 */
@Component("taskAttributeFacade")
public class TaskAttributeFacadeImpl extends AbstractFacade implements TaskAttributeFacade {

    @Autowired
    private TaskAttributeService taskAttributeService;

    @Override
    public TaskResult<List<TaskAttributeRO>> queryTaskAttribute(TaskAttributeRequest request) {
        logger.info("查询任务变量信息，传入的请求参数为{}", request);
        TaskAttributeQuery query = new TaskAttributeQuery();
        query.setId(request.getId());
        query.setTaskIds(request.getTaskIds());
        query.setName(request.getName());
        query.setValue(request.getValue());

        List<TaskAttribute> list = taskAttributeService.queryAttributes(query);

        List<TaskAttributeRO> attributeROList = convert(list, TaskAttributeRO.class);

        return TaskResult.wrapSuccessfulResult(attributeROList);
    }

    @Override
    public TaskResult<List<TaskAttributeRO>> queryTaskAttributeByTaskId(TaskAttributeRequest request) {
        List<TaskAttribute> list = taskAttributeService.listAttributesInTaskIdsAndByName(request.getTaskIds(), request.getName());

        List<TaskAttributeRO> taskAttributeROS = convert(list, TaskAttributeRO.class);

        return TaskResult.wrapSuccessfulResult(taskAttributeROS);
    }

    @Override
    public TaskResult<Map<String, TaskAttributeRO>> findByNames(Long taskId, boolean decrypt, String... names) {
        Map<String, TaskAttributeRO> result;

        List<TaskAttribute> attributes = taskAttributeService.listAttributesByTaskIdAndInNames(taskId, names, decrypt);
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
        TaskAttribute taskAttribute = taskAttributeService.queryAttributeByTaskIdAndName(taskId, name, decrypt);
        if (taskAttribute == null) {
            return TaskResult.wrapSuccessfulResult(null);
        }
        TaskAttributeRO attributeRO = convert(taskAttribute, TaskAttributeRO.class);
        return TaskResult.wrapSuccessfulResult(attributeRO);
    }

    @Override
    public TaskResult<TaskAttributeRO> findByNameAndValue(String name, String value, boolean encrypt) {
        TaskAttribute taskAttribute = taskAttributeService.queryAttributeByNameAndValue(name, value, encrypt);
        TaskAttributeRO taskAttributeRO = convert(taskAttribute, TaskAttributeRO.class);
        return TaskResult.wrapSuccessfulResult(taskAttributeRO);
    }

    @Override
    public TaskResult<List<TaskAttributeRO>> findByTaskId(Long taskId) {
        List<TaskAttribute> taskAttributeList = taskAttributeService.listAttributesByTaskId(taskId);
        List<TaskAttributeRO> taskAttributeROList = convert(taskAttributeList, TaskAttributeRO.class);
        return TaskResult.wrapSuccessfulResult(taskAttributeROList);
    }

    @Override
    public TaskResponse<List<AttributeDTO>> batchQueryAttribute(AttributeBatchQueryRequest request) {
        List<TaskAttribute> attributes = queryAttributesInTaskIdsAndByName(request);

        List<AttributeDTO> result = convert(attributes, AttributeDTO.class);

        return TaskResponse.success(result);
    }

    private List<TaskAttribute> queryAttributesInTaskIdsAndByName(AttributeBatchQueryRequest request) {
        Preconditions.notNull("request", request);
        Preconditions.notEmpty("request.taskIds", request.getTaskIds());
        Preconditions.notEmpty("request.name", request.getName());
        return taskAttributeService.listAttributesInTaskIdsAndByName(request.getTaskIds(), request.getName(), request.isSensitive());
    }

    @Override
    public TaskResponse<Map<Long, String>> batchQueryAttributeAsMap(AttributeBatchQueryRequest request) {
        List<TaskAttribute> attributes = queryAttributesInTaskIdsAndByName(request);
        Map<Long, String> result;
        if (CollectionUtils.isNotEmpty(attributes)) {
            result = attributes.stream().collect(Collectors.toMap(TaskAttribute::getTaskId, TaskAttribute::getValue, (a, b) -> b));
        } else {
            result = Collections.emptyMap();
        }
        return TaskResponse.success(result);
    }

    @Override
    public TaskResponse<List<SimpleAttributeDTO>> queryAttributes(Long taskId, String... names) {
        Preconditions.notNull("taskId", taskId);
        Preconditions.notEmpty("names", names);
        final List<TaskAttribute> attributes = taskAttributeService.listAttributesByTaskIdAndInNames(taskId, names, false);

        List<SimpleAttributeDTO> result = convert(attributes, SimpleAttributeDTO.class);

        return TaskResponse.success(result);
    }

    @Override
    public TaskResponse<List<SimpleAttributeDTO>> querySensitiveAttributes(Long taskId, String... names) {
        Preconditions.notNull("taskId", taskId);
        Preconditions.notEmpty("names", names);
        final List<TaskAttribute> attributes = taskAttributeService.listAttributesByTaskIdAndInNames(taskId, names, true);

        List<SimpleAttributeDTO> result = convert(attributes, SimpleAttributeDTO.class);

        return TaskResponse.success(result);
    }

    @Override
    public TaskResponse<List<SimpleAttributeDTO>> queryAttributes(MultiAttributeQueryRequest request) {
        Preconditions.notNull("request", request);
        Preconditions.notNull("request.taskId", request.getTaskId());
        Preconditions.notEmpty("request.nameCondition", request.getNameCondition());
        Map<String, Boolean> nameCondition = request.getNameCondition();
        Map<Boolean, Set<String>> conditions =
            nameCondition.entrySet().stream().collect(Collectors.groupingBy(Entry::getValue, Collectors.mapping(Entry::getKey, Collectors.toSet())));

        List<SimpleAttributeDTO> result = new ArrayList<>(nameCondition.size());
        conditions.forEach((sensitive, names) -> {
            List<TaskAttribute> attributes = taskAttributeService.listAttributesByTaskIdAndInNames(request.getTaskId(), names.toArray(new String[0]), sensitive);
            if (CollectionUtils.isNotEmpty(attributes)) {
                SimpleAttributeDTO item;
                for (TaskAttribute attribute : attributes) {
                    item = convert(attribute, SimpleAttributeDTO.class);
                    if (item != null) {
                        result.add(item);
                    }
                }
            }
        });

        return TaskResponse.success(result);
    }

    @Override
    public TaskResponse<Map<String, String>> queryAttributesAsMap(Long taskId, String... names) {
        Preconditions.notNull("taskId", taskId);
        Preconditions.notEmpty("names", names);
        Map<String, String> result = taskAttributeService.getAttributeMapByTaskIdAndInNames(taskId, names, false);

        return TaskResponse.success(result);
    }

    @Override
    public TaskResponse<Map<String, String>> querySensitiveAttributesAsMap(Long taskId, String... names) {
        Preconditions.notNull("taskId", taskId);
        Preconditions.notEmpty("names", names);
        Map<String, String> result = taskAttributeService.getAttributeMapByTaskIdAndInNames(taskId, names, true);

        return TaskResponse.success(result);
    }

    @Override
    public TaskResponse<Map<String, String>> queryAttributesAsMap(MultiAttributeQueryRequest request) {
        Preconditions.notNull("request", request);
        Preconditions.notNull("request.taskId", request.getTaskId());
        Preconditions.notEmpty("request.nameCondition", request.getNameCondition());
        Map<String, Boolean> nameCondition = request.getNameCondition();
        Map<Boolean, Set<String>> conditions =
            nameCondition.entrySet().stream().collect(Collectors.groupingBy(Entry::getValue, Collectors.mapping(Entry::getKey, Collectors.toSet())));

        Map<String, String> result = new HashMap<>(nameCondition.size());
        conditions.forEach((sensitive, names) -> {
            List<TaskAttribute> attributes = taskAttributeService.listAttributesByTaskIdAndInNames(request.getTaskId(), names.toArray(new String[0]), sensitive);
            if (CollectionUtils.isNotEmpty(attributes)) {
                for (TaskAttribute attribute : attributes) {
                    result.put(attribute.getName(), attribute.getValue());
                }
            }
        });

        return TaskResponse.success(result);
    }

    @Override
    public TaskResponse<String> queryAttributeValue(Long taskId, String name, boolean sensitive) {
        Preconditions.notNull("taskId", taskId);
        String value = null;
        if (StringUtils.isNotEmpty(name)) {
            TaskAttribute taskAttribute = taskAttributeService.queryAttributeByTaskIdAndName(taskId, name, sensitive);
            value = taskAttribute != null ? taskAttribute.getValue() : null;
        }
        return TaskResponse.success(value);
    }

    @Override
    public TaskResponse<Void> saveAttribute(Long taskId, String name, String value, boolean sensitive) {
        Preconditions.notNull("taskId", taskId);
        if (StringUtils.isNotEmpty(name)) {
            taskAttributeService.insertOrUpdate(taskId, name, value, sensitive);
        }
        return TaskResponse.success(null);
    }

    @Override
    public TaskResponse<Void> saveAttributes(TaskAttributesSaveRequest request) {
        Preconditions.notNull("request", request);
        Preconditions.notNull("request.taskId", request.getTaskId());

        if (!request.isEmpty()) {
            final List<SavedTaskAttribute> attributes = request.getAttributes();
            taskAttributeService.saveAttributes(request.getTaskId(), convert(attributes, TaskAttributeSaveObject.class));
        }

        return TaskResponse.success(null);
    }

    @Override
    public TaskResponse<Void> saveLoginTime(Long taskId, Date loginTime) {
        Preconditions.notNull("taskId", taskId);
        if (loginTime != null) {
            taskAttributeService.saveLoginTime(taskId, loginTime);
        }
        return TaskResponse.success(null);
    }

    @Override
    public TaskResponse<Date> queryLoginTime(Long taskId) {
        Preconditions.notNull("taskId", taskId);

        Date loginTime = taskAttributeService.queryLoginTime(taskId);

        return TaskResponse.success(loginTime);
    }
}
