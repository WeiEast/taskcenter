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

package com.treefinance.saas.taskcenter.dao.repository.impl;

import com.treefinance.saas.taskcenter.dao.entity.TaskAttribute;
import com.treefinance.saas.taskcenter.dao.entity.TaskAttributeCriteria;
import com.treefinance.saas.taskcenter.dao.mapper.TaskAttributeMapper;
import com.treefinance.saas.taskcenter.dao.mapper.TaskAttributeUpdateMapper;
import com.treefinance.saas.taskcenter.dao.param.TaskAttributeQuery;
import com.treefinance.saas.taskcenter.dao.repository.TaskAttributeRepository;
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
    public Map<String, String> getAttributeMapByTaskId(@Nonnull Long taskId, boolean decrypt) {
        List<TaskAttribute> attributes = this.listAttributesByTaskId(taskId);

        return toMap(attributes, decrypt);
    }

    @Override
    public Map<String, String> getAttributeMapByTaskIdAndInNames(@Nonnull Long taskId, @Nonnull List<String> names, boolean decrypt) {
        List<TaskAttribute> attributes = this.listAttributesByTaskIdAndInNames(taskId, names);

        return toMap(attributes, decrypt);
    }

    private Map<String, String> toMap(List<TaskAttribute> attributes, boolean decrypt) {
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
    public TaskAttribute queryAttributeByTaskIdAndName(@Nonnull Long taskId, @Nonnull String name, boolean decrypt) {
        TaskAttribute attribute = this.queryAttributeByTaskIdAndName(taskId, name);
        if (decrypt && attribute != null) {
            attribute.setValue(decryptNormal(attribute.getValue()));
        }

        return attribute;
    }

    @Override
    public TaskAttribute queryAttributeByTaskIdAndName(@Nonnull Long taskId, @Nonnull String name) {
        List<TaskAttribute> attributes = this.listAttributesByTaskIdAndName(taskId, name);
        return CollectionUtils.isEmpty(attributes) ? null : attributes.get(0);
    }

    @Override
    public TaskAttribute queryAttributeByNameAndValue(@Nonnull String name, @Nonnull String value, boolean encrypt) {
        List<TaskAttribute> attributes = this.listAttributesByNameAndValue(name, value, encrypt);

        return CollectionUtils.isEmpty(attributes) ? null : attributes.get(0);
    }

    @Override
    public List<TaskAttribute> listAttributesByNameAndValue(@Nonnull String name, @Nonnull String value, boolean encrypt) {
        String val = encryptNormal(value, encrypt);
        TaskAttributeCriteria criteria = new TaskAttributeCriteria();
        criteria.createCriteria().andNameEqualTo(name).andValueEqualTo(val);
        return taskAttributeMapper.selectByExample(criteria);
    }

    @Override
    public List<TaskAttribute> listAttributesByTaskId(@Nonnull Long taskId) {
        TaskAttributeCriteria criteria = new TaskAttributeCriteria();
        criteria.createCriteria().andTaskIdEqualTo(taskId);
        return taskAttributeMapper.selectByExample(criteria);
    }

    @Override
    public List<TaskAttribute> listAttributesByTaskIdAndName(@Nonnull Long taskId, @Nonnull String name) {
        TaskAttributeCriteria criteria = new TaskAttributeCriteria();
        criteria.createCriteria().andTaskIdEqualTo(taskId).andNameEqualTo(name);
        return taskAttributeMapper.selectByExample(criteria);
    }

    @Override
    public List<TaskAttribute> listAttributesByTaskIdAndInNames(@Nonnull Long taskId, @Nonnull List<String> names) {
        TaskAttributeCriteria criteria = new TaskAttributeCriteria();
        criteria.createCriteria().andTaskIdEqualTo(taskId).andNameIn(names);
        return taskAttributeMapper.selectByExample(criteria);
    }

    @Override
    public List<TaskAttribute> listAttributesByTaskIdAndInNames(@Nonnull Long taskId, @Nonnull List<String> names, boolean decrypt) {
        List<TaskAttribute> attributes = this.listAttributesByTaskIdAndInNames(taskId, names);
        if (decrypt && CollectionUtils.isNotEmpty(attributes)) {
            for (TaskAttribute attribute : attributes) {
                attribute.setValue(decryptNormal(attribute.getValue()));
            }
        }

        return attributes;
    }

    @Override
    public List<TaskAttribute> listAttributesInTaskIdsAndByName(@Nonnull List<Long> taskIds, @Nonnull String name) {
        TaskAttributeCriteria taskAttributeCriteria = new TaskAttributeCriteria();
        taskAttributeCriteria.createCriteria().andTaskIdIn(taskIds).andNameEqualTo(name);

        return taskAttributeMapper.selectByExample(taskAttributeCriteria);
    }

    @Override
    public List<TaskAttribute> listAttributesInTaskIdsAndByName(@Nonnull List<Long> taskIds, @Nonnull String name, boolean decrypt) {
        List<TaskAttribute> attributes = this.listAttributesInTaskIdsAndByName(taskIds, name);
        if (decrypt && CollectionUtils.isNotEmpty(attributes)) {
            for (TaskAttribute attribute : attributes) {
                attribute.setValue(decryptNormal(attribute.getValue()));
            }
        }

        return attributes;
    }

    @Override
    public List<TaskAttribute> queryAttributes(@Nonnull TaskAttributeQuery query) {
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
    public Long insertAttribute(@Nonnull Long taskId, @Nonnull String name, @Nullable String value, boolean encrypt) {
        TaskAttribute attribute = new TaskAttribute();
        attribute.setId(generateUniqueId());
        attribute.setTaskId(taskId);
        attribute.setName(name);
        attribute.setValue(encryptNormal(value, encrypt));
        taskAttributeMapper.insertSelective(attribute);
        return attribute.getId();
    }

    @Override
    public void insertOrUpdateAttribute(@Nonnull Long taskId, @Nonnull String name, @Nullable String value, boolean encrypt) {
        TaskAttribute taskAttribute = new TaskAttribute();
        taskAttribute.setId(generateUniqueId());
        taskAttribute.setTaskId(taskId);
        taskAttribute.setName(name);
        taskAttribute.setValue(encryptNormal(value, encrypt));
        taskAttributeUpdateMapper.insertOrUpdateSelective(taskAttribute);
    }

    @Override
    public int deleteAttributeByTaskIdAndName(@Nonnull Long taskId, @Nonnull String name) {
        TaskAttributeCriteria taskAttributeCriteria = new TaskAttributeCriteria();
        taskAttributeCriteria.createCriteria().andTaskIdEqualTo(taskId).andNameEqualTo(name);
        return taskAttributeMapper.deleteByExample(taskAttributeCriteria);
    }

}
