/*
 * Copyright © 2015 - 2017 杭州大树网络技术有限公司. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.treefinance.saas.taskcenter.biz.service.impl;

import com.treefinance.saas.taskcenter.biz.service.TaskAttributeService;
import com.treefinance.saas.taskcenter.dao.param.TaskAttributeQuery;
import com.treefinance.saas.taskcenter.dao.entity.TaskAttribute;
import com.treefinance.saas.taskcenter.dao.repository.TaskAttributeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by chenjh on 2017/7/5.
 * <p>
 * 任务拓展属性业务层
 */
@Service
public class TaskAttributeServiceImpl implements TaskAttributeService {
    @Autowired
    private TaskAttributeRepository taskAttributeRepository;

    @Override
    public TaskAttribute findByName(Long taskId, String name, boolean decrypt) {
        return taskAttributeRepository.getTaskAttributeByTaskIdAndName(taskId, name, decrypt);
    }

    @Override
    public TaskAttribute findByNameAndValue(String name, String value, boolean encrypt) {
        return taskAttributeRepository.getTaskAttributeByNameAndValue(name, value, encrypt);
    }

    @Override
    public Map<String, String> getAttributeMapByTaskIdAndInNames(@Nonnull Long taskId, @Nonnull String[] names, boolean decrypt) {
        return taskAttributeRepository.getAttributeMapByTaskIdAndInNames(taskId, Arrays.asList(names), decrypt);
    }

    @Override
    public List<TaskAttribute> listTaskAttributesByTaskIdAndInNames(@Nonnull Long taskId, @Nonnull String[] names, boolean decrypt) {
        return taskAttributeRepository.listTaskAttributesByTaskIdAndInNames(taskId, Arrays.asList(names), decrypt);
    }

    @Override
    public Long insert(@Nonnull Long taskId, @Nonnull String name, @Nullable String value, boolean encrypt) {
        return taskAttributeRepository.insertTagAttribute(taskId, name, value, encrypt);
    }

    @Override
    public void insertOrUpdate(@Nonnull Long taskId, @Nonnull String name, @Nullable String value, boolean encrypt) {
        taskAttributeRepository.insertOrUpdateTagAttribute(taskId, name, value, encrypt);
    }

    @Override
    public List<TaskAttribute> findByTaskId(Long taskId) {
        return taskAttributeRepository.listTaskAttributesByTaskId(taskId);
    }

    @Override
    public void deleteByTaskIdAndName(Long taskId, String name) {
        taskAttributeRepository.deleteByTaskIdAndName(taskId, name);
    }

    @Override
    public List<TaskAttribute> listTaskAttributesByNameAndInTaskIds(@Nonnull String name, @Nonnull List<Long> taskIds) {
        return taskAttributeRepository.listTaskAttributesByNameAndInTaskIds(name, taskIds);
    }

    @Override
    public List<TaskAttribute> queryTaskAttributes(@Nonnull TaskAttributeQuery query) {
        return taskAttributeRepository.queryTaskAttributes(query);
    }
}
