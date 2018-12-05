package com.treefinance.saas.taskcenter.facade.service;

import com.treefinance.saas.taskcenter.facade.request.TaskAttributeRequest;
import com.treefinance.saas.taskcenter.facade.result.TaskAttributeRO;
import com.treefinance.saas.taskcenter.facade.result.common.TaskResult;

import java.util.List;
import java.util.Map;

/**
 * @author:guoguoyun
 * @date:Created in 2018/9/18上午11:01
 */
public interface TaskAttributeFacade {

    TaskResult<List<TaskAttributeRO>> queryTaskAttribute(TaskAttributeRequest taskAttributeRequest);


    TaskResult<List<TaskAttributeRO>> queryTaskAttributeByTaskId(TaskAttributeRequest taskAttributeRequest);


    TaskResult<Map<String, TaskAttributeRO>> findByNames(Long taskId, boolean decrypt, String... names);


    TaskResult<Long> insert(Long taskId, String name, String value);

    /**
     * 保存属性
     *
     * @param taskId 任务ID
     * @param name 属性名
     * @param value 属性值
     * @param sensitive 是否是敏感值
     * @return primary key task_attribute
     */
    TaskResult<Long> insert(Long taskId, String name, String value, boolean sensitive);

    TaskResult<Void> insertOrUpdateSelective(Long taskId, String name, String value);


    TaskResult<TaskAttributeRO> findByName(Long taskId, String name, boolean decrypt);


    TaskResult<TaskAttributeRO> findByNameAndValue(String name, String value, boolean encrypt);


    TaskResult<List<TaskAttributeRO>> findByTaskId(Long taskId);


}
