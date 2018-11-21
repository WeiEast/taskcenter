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

import com.treefinance.basicservice.security.crypto.facade.EncryptionIntensityEnum;
import com.treefinance.basicservice.security.crypto.facade.ISecurityCryptoService;
import com.treefinance.commonservice.uid.UidService;
import com.treefinance.saas.taskcenter.dao.domain.TaskAttributeQuery;
import com.treefinance.saas.taskcenter.dao.entity.TaskAttribute;
import com.treefinance.saas.taskcenter.dao.entity.TaskAttributeCriteria;
import com.treefinance.saas.taskcenter.dao.mapper.TaskAttributeMapper;
import com.treefinance.saas.taskcenter.dao.mapper.TaskAttributeUpdateMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jerry
 * @date 2018/11/20 20:49
 */
@Repository
public class TaskAttributeRepositoryImpl implements TaskAttributeRepository {

    @Autowired
    private TaskAttributeMapper taskAttributeMapper;
    @Autowired
    private TaskAttributeUpdateMapper taskAttributeUpdateMapper;
    @Autowired
    private UidService uidService;
    @Autowired
    private ISecurityCryptoService securityCryptoService;

    @Override
    public Map<String, String> getAttributeMapByTaskIdAndInNames(@Nonnull Long taskId, @Nonnull List<String> names, boolean decrypt) {
        List<TaskAttribute> attributes = listTaskAttributesByTaskIdAndInNames(taskId, names);
        if (CollectionUtils.isNotEmpty(attributes)) {
            Map<String, String> map = new HashMap<>();
            for (TaskAttribute attribute : attributes) {
                String value = attribute.getValue();
                if (decrypt && StringUtils.isNotEmpty(value)) {
                    value = securityCryptoService.decrypt(value, EncryptionIntensityEnum.NORMAL);
                }
                map.put(attribute.getName(), value);
            }
            return map;
        }

        return Collections.emptyMap();
    }

    @Override
    public TaskAttribute getTaskAttributeByTaskIdAndName(@Nonnull Long taskId, @Nonnull String name, boolean decrypt) {
        TaskAttribute attribute = getTaskAttributeByTaskIdAndName(taskId, name);
        if (attribute != null && decrypt && StringUtils.isNotEmpty(attribute.getValue())) {
            String value = securityCryptoService.decrypt(attribute.getValue(), EncryptionIntensityEnum.NORMAL);

            attribute.setValue(value);
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
        String val = value;
        if (encrypt && StringUtils.isNotEmpty(val)) {
            val = securityCryptoService.encrypt(val, EncryptionIntensityEnum.NORMAL);
        }
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
                if (decrypt && StringUtils.isNotEmpty(attribute.getValue())) {
                    String value = securityCryptoService.decrypt(attribute.getValue(), EncryptionIntensityEnum.NORMAL);
                    attribute.setValue(value);
                }
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
        attribute.setId(uidService.getId());
        attribute.setTaskId(taskId);
        attribute.setName(name);
        attribute.setValue(processEncryption(value, encrypt));
        taskAttributeMapper.insertSelective(attribute);
        return attribute.getId();
    }

    @Override
    public void insertOrUpdateTagAttribute(@Nonnull Long taskId, @Nonnull String name, @Nullable String value, boolean encrypt) {
        TaskAttribute taskAttribute = new TaskAttribute();
        taskAttribute.setId(uidService.getId());
        taskAttribute.setTaskId(taskId);
        taskAttribute.setName(name);
        taskAttribute.setValue(processEncryption(value, encrypt));
        taskAttributeUpdateMapper.insertOrUpdateSelective(taskAttribute);
    }

    private String processEncryption(String val, boolean encrypt) {
        if (encrypt && StringUtils.isNotEmpty(val)) {
            return securityCryptoService.encrypt(val, EncryptionIntensityEnum.NORMAL);
        }

        return val;
    }

    @Override
    public int deleteByTaskIdAndName(@Nonnull Long taskId, @Nonnull String name) {
        TaskAttributeCriteria taskAttributeCriteria = new TaskAttributeCriteria();
        taskAttributeCriteria.createCriteria().andTaskIdEqualTo(taskId).andNameEqualTo(name);
        return taskAttributeMapper.deleteByExample(taskAttributeCriteria);
    }

}
