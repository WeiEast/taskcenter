/*
 * Copyright © 2015 - 2017 杭州大树网络技术有限公司. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.treefinance.saas.taskcenter.dao.repository;

import com.treefinance.saas.taskcenter.dao.entity.TaskAttribute;
import com.treefinance.saas.taskcenter.dao.entity.TaskAttributeCriteria;
import com.treefinance.saas.taskcenter.dao.mapper.TaskAttributeMapper;
import com.treefinance.saas.taskcenter.dao.mapper.TaskAttributeUpdateMapper;
import com.treefinance.saas.taskcenter.dao.param.TaskAttributeQuery;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Jerry
 * @date 2018/11/20 20:49
 */
@Repository
public class TaskAttributeRepositoryImpl extends AbstractRepository implements TaskAttributeRepository {

    @Autowired
    private TaskAttributeMapper taskAttributeMapper;
    @Autowired
    private TaskAttributeUpdateMapper taskAttributeUpdateMapper;

    @Override
    public Map<String, String> getAttributeMapByTaskIdAndInNames(@Nonnull Long taskId, @Nonnull List<String> names, boolean decrypt) {
        List<TaskAttribute> attributes = listTaskAttributesByTaskIdAndInNames(taskId, names);
        if (CollectionUtils.isNotEmpty(attributes)) {
            if (decrypt) {
                return attributes.stream().collect(Collectors.toMap(TaskAttribute::getName, attribute -> decryptNormal(attribute.getValue()), (a, b) -> b));
            } else {
                return attributes.stream().collect(Collectors.toMap(TaskAttribute::getName, TaskAttribute::getValue, (a, b) -> b));
            }
        }

        return Collections.emptyMap();
    }

    @Override
    public TaskAttribute getTaskAttributeByTaskIdAndName(@Nonnull Long taskId, @Nonnull String name, boolean decrypt) {
        TaskAttribute attribute = getTaskAttributeByTaskIdAndName(taskId, name);
        if (attribute != null && decrypt) {
            attribute.setValue(decryptNormal(attribute.getValue()));
        }

        return attribute;
    }

    @Override
    public TaskAttribute getTaskAttributeByTaskIdAndName(@Nonnull Long taskId, @Nonnull String name) {
        List<TaskAttribute> attributes = listTaskAttributesByTaskIdAndName(taskId, name);
        return CollectionUtils.isEmpty(attributes) ? null : attributes.get(0);
    }

    @Override
    public TaskAttribute getTaskAttributeByNameAndValue(@Nonnull String name, @Nonnull String value, boolean encrypt) {
        List<TaskAttribute> attributes = listTaskAttributesByNameAndValue(name, value, encrypt);

        return CollectionUtils.isEmpty(attributes) ? null : attributes.get(0);
    }

    @Override
    public List<TaskAttribute> listTaskAttributesByNameAndValue(@Nonnull String name, @Nonnull String value, boolean encrypt) {
        String val = encryptNormal(value, encrypt);
        TaskAttributeCriteria criteria = new TaskAttributeCriteria();
        criteria.createCriteria().andNameEqualTo(name).andValueEqualTo(val);
        return taskAttributeMapper.selectByExample(criteria);
    }

    @Override
    public List<TaskAttribute> listTaskAttributesByTaskId(@Nonnull Long taskId) {
        TaskAttributeCriteria criteria = new TaskAttributeCriteria();
        criteria.createCriteria().andTaskIdEqualTo(taskId);
        return taskAttributeMapper.selectByExample(criteria);
    }

    @Override
    public List<TaskAttribute> listTaskAttributesByTaskIdAndName(@Nonnull Long taskId, @Nonnull String name) {
        TaskAttributeCriteria criteria = new TaskAttributeCriteria();
        criteria.createCriteria().andTaskIdEqualTo(taskId).andNameEqualTo(name);
        return taskAttributeMapper.selectByExample(criteria);
    }

    @Override
    public List<TaskAttribute> listTaskAttributesByTaskIdAndInNames(@Nonnull Long taskId, @Nonnull List<String> names) {
        TaskAttributeCriteria criteria = new TaskAttributeCriteria();
        criteria.createCriteria().andTaskIdEqualTo(taskId).andNameIn(names);
        return taskAttributeMapper.selectByExample(criteria);
    }

    @Override
    public List<TaskAttribute> listTaskAttributesByTaskIdAndInNames(@Nonnull Long taskId, @Nonnull List<String> names, boolean decrypt) {
        List<TaskAttribute> attributes = listTaskAttributesByTaskIdAndInNames(taskId, names);
        if (CollectionUtils.isNotEmpty(attributes)) {
            for (TaskAttribute attribute : attributes) {
                attribute.setValue(decryptNormal(attribute.getValue(), decrypt));
            }
        }

        return attributes;
    }

    @Override
    public List<TaskAttribute> listTaskAttributesByNameAndInTaskIds(@Nonnull String name, @Nonnull List<Long> taskIds) {
        TaskAttributeCriteria taskAttributeCriteria = new TaskAttributeCriteria();
        taskAttributeCriteria.createCriteria().andNameEqualTo(name).andTaskIdIn(taskIds);

        return taskAttributeMapper.selectByExample(taskAttributeCriteria);
    }

    @Override
    public List<TaskAttribute> queryTaskAttributes(@Nonnull TaskAttributeQuery query) {
        TaskAttributeCriteria taskAttributeCriteria = new TaskAttributeCriteria();
        TaskAttributeCriteria.Criteria criteria = taskAttributeCriteria.createCriteria();
        Long id = query.getId();
        if (id != null) {
            criteria.andIdEqualTo(id);
        }
        List<Long> taskIds = query.getTaskIds();
        if (CollectionUtils.isNotEmpty(taskIds)) {
            criteria.andTaskIdIn(taskIds);
        }
        String name = query.getName();
        if (StringUtils.isNotEmpty(name)) {
            criteria.andNameEqualTo(name);
        }

        String value = query.getValue();
        if (StringUtils.isNotEmpty(value)) {
            criteria.andValueEqualTo(value);
        }

        return taskAttributeMapper.selectByExample(taskAttributeCriteria);
    }

    @Override
    public Long insertTagAttribute(@Nonnull Long taskId, @Nonnull String name, @Nullable String value, boolean encrypt) {
        TaskAttribute attribute = new TaskAttribute();
        attribute.setId(generateUniqueId());
        attribute.setTaskId(taskId);
        attribute.setName(name);
        attribute.setValue(encryptNormal(value, encrypt));
        taskAttributeMapper.insertSelective(attribute);
        return attribute.getId();
    }

    @Override
    public void insertOrUpdateTagAttribute(@Nonnull Long taskId, @Nonnull String name, @Nullable String value, boolean encrypt) {
        TaskAttribute taskAttribute = new TaskAttribute();
        taskAttribute.setId(generateUniqueId());
        taskAttribute.setTaskId(taskId);
        taskAttribute.setName(name);
        taskAttribute.setValue(encryptNormal(value, encrypt));
        taskAttributeUpdateMapper.insertOrUpdateSelective(taskAttribute);
    }

    @Override
    public int deleteByTaskIdAndName(@Nonnull Long taskId, @Nonnull String name) {
        TaskAttributeCriteria taskAttributeCriteria = new TaskAttributeCriteria();
        taskAttributeCriteria.createCriteria().andTaskIdEqualTo(taskId).andNameEqualTo(name);
        return taskAttributeMapper.deleteByExample(taskAttributeCriteria);
    }

}
