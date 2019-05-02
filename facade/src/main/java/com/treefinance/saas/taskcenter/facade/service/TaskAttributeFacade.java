package com.treefinance.saas.taskcenter.facade.service;

import com.treefinance.saas.taskcenter.facade.request.AttributeBatchQueryRequest;
import com.treefinance.saas.taskcenter.facade.request.MultiAttributeQueryRequest;
import com.treefinance.saas.taskcenter.facade.request.TaskAttributeRequest;
import com.treefinance.saas.taskcenter.facade.response.TaskResponse;
import com.treefinance.saas.taskcenter.facade.result.AttributeDTO;
import com.treefinance.saas.taskcenter.facade.result.SimpleAttributeDTO;
import com.treefinance.saas.taskcenter.facade.result.TaskAttributeRO;
import com.treefinance.saas.taskcenter.facade.result.common.TaskResult;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author:guoguoyun
 * @date:Created in 2018/9/18上午11:01
 */
public interface TaskAttributeFacade {

    @Deprecated
    TaskResult<List<TaskAttributeRO>> queryTaskAttribute(TaskAttributeRequest taskAttributeRequest);

    @Deprecated
    TaskResult<List<TaskAttributeRO>> queryTaskAttributeByTaskId(TaskAttributeRequest taskAttributeRequest);

    @Deprecated
    TaskResult<TaskAttributeRO> findByName(Long taskId, String name, boolean decrypt);

    @Deprecated
    TaskResult<Map<String, TaskAttributeRO>> findByNames(Long taskId, boolean decrypt, String... names);

    @Deprecated
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
    @Deprecated
    TaskResult<Long> insert(Long taskId, String name, String value, boolean sensitive);

    /**
     * @deprecated use {@link #saveAttribute(Long, String, String, boolean)} instead
     */
    @Deprecated
    TaskResult<Void> insertOrUpdateSelective(Long taskId, String name, String value);

    @Deprecated
    TaskResult<TaskAttributeRO> findByNameAndValue(String name, String value, boolean encrypt);

    @Deprecated
    TaskResult<List<TaskAttributeRO>> findByTaskId(Long taskId);

    /**
     * 批量查询多个任务的指定属性值
     *
     * @param request 查询条件
     * @return a list of {@link AttributeDTO}
     */
    TaskResponse<List<AttributeDTO>> batchQueryAttribute(AttributeBatchQueryRequest request);

    /**
     * 批量查询多个任务的指定属性值
     *
     * @param request 查询条件
     * @return a map with "Attribute:taskId":"Attribute:value"
     */
    TaskResponse<Map<Long, String>> batchQueryAttributeAsMap(AttributeBatchQueryRequest request);

    /**
     * 查询单任务的多个属性值。注意：查询属性为非敏感字段，不作任何加解密。
     *
     * @param taskId 任务ID
     * @param names 查询的属性名
     * @return a list of {@link SimpleAttributeDTO}
     */
    TaskResponse<List<SimpleAttributeDTO>> queryAttributes(Long taskId, String... names);

    /**
     * 查询单任务的多个属性值。注意：查询属性为敏感字段，需要加解密。
     *
     * @param taskId 任务ID
     * @param names 查询的属性名
     * @return a list of {@link SimpleAttributeDTO}
     */
    TaskResponse<List<SimpleAttributeDTO>> querySensitiveAttributes(Long taskId, String... names);

    /**
     * 查询单任务的多个属性值
     *
     * @param request 查询条件
     * @return a list of {@link SimpleAttributeDTO}
     */
    TaskResponse<List<SimpleAttributeDTO>> queryAttributes(MultiAttributeQueryRequest request);

    /**
     * 查询单任务的多个属性值。注意：查询属性为非敏感字段，不作任何加解密。
     *
     * @param taskId 任务ID
     * @param names 查询的属性名
     * @return a map like "Attribute:name":"Attribute:value"
     */
    TaskResponse<Map<String, String>> queryAttributesAsMap(Long taskId, String... names);

    /**
     * 查询单任务的多个属性值。注意：查询属性为敏感字段，需要加解密。
     *
     * @param taskId 任务ID
     * @param names 查询的属性名
     * @return a map like "Attribute:name":"Attribute:value"
     */
    TaskResponse<Map<String, String>> querySensitiveAttributesAsMap(Long taskId, String... names);

    /**
     * 查询单任务的多个属性值
     *
     * @param request 查询条件
     * @return a map with "Attribute:name":"Attribute:value"
     */
    TaskResponse<Map<String, String>> queryAttributesAsMap(MultiAttributeQueryRequest request);

    /**
     * 查询属性值
     *
     * @param taskId 任务ID
     * @param name 属性名
     * @param sensitive 是否敏感，是说明值是加密保存，需要解密
     * @return 属性值
     */
    TaskResponse<String> queryAttributeValue(Long taskId, String name, boolean sensitive);

    /**
     * 保存属性值
     *
     * @param taskId 任务ID
     * @param name 属性名
     * @param value 属性值
     * @param sensitive 是否是敏感字段，是说明值是加密保存
     * @return Void
     */
    TaskResponse<Void> saveAttribute(Long taskId, String name, String value, boolean sensitive);

    /**
     * 保存任务登录时间
     *
     * @param taskId 任务ID
     * @param loginTime 登录时间
     * @return Void
     */
    TaskResponse<Void> saveLoginTime(Long taskId, Date loginTime);

    /**
     * 查询登录时间
     *
     * @param taskId 任务ID
     * @return 登录时间
     */
    TaskResponse<Date> queryLoginTime(Long taskId);
}
