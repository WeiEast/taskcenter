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

import com.treefinance.saas.taskcenter.dao.entity.TaskBuryPointLog;
import com.treefinance.saas.taskcenter.dao.entity.TaskBuryPointLogCriteria;
import com.treefinance.saas.taskcenter.dao.mapper.TaskBuryPointLogMapper;
import com.treefinance.saas.taskcenter.dao.repository.TaskBuryPointRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.Date;
import java.util.List;

/**
 * @author Jerry
 * @date 2018/11/21 16:27
 */
@Repository
public class TaskBuryPointRepositoryImpl extends AbstractRepository implements TaskBuryPointRepository {
    @Autowired
    private TaskBuryPointLogMapper taskBuryPointLogMapper;

    @Override
    public List<TaskBuryPointLog> queryTaskBuryPointLogsByTaskIdAndInCodes(@Nonnull Long taskId, @Nullable List<String> codes) {
        TaskBuryPointLogCriteria taskBuryPointLogCriteria = new TaskBuryPointLogCriteria();
        TaskBuryPointLogCriteria.Criteria criteria = taskBuryPointLogCriteria.createCriteria().andTaskIdEqualTo(taskId);
        if (CollectionUtils.isNotEmpty(codes)) {
            criteria.andCodeIn(codes);
        }
        return taskBuryPointLogMapper.selectByExample(taskBuryPointLogCriteria);
    }

    @Override
    public long countTaskBuryPointLogsByTaskIdAndInCodes(@Nonnull Long taskId, @Nullable List<String> codes) {
        TaskBuryPointLogCriteria taskBuryPointLogCriteria = new TaskBuryPointLogCriteria();
        TaskBuryPointLogCriteria.Criteria criteria = taskBuryPointLogCriteria.createCriteria().andTaskIdEqualTo(taskId);
        if (CollectionUtils.isNotEmpty(codes)) {
            criteria.andCodeIn(codes);
        }
        return taskBuryPointLogMapper.countByExample(taskBuryPointLogCriteria);
    }

    @Override
    public List<TaskBuryPointLog> queryTaskBuryPointLogs(Long id, String appId, Long taskId, String code, String order) {
        TaskBuryPointLogCriteria taskBuryPointLogCriteria = new TaskBuryPointLogCriteria();
        TaskBuryPointLogCriteria.Criteria criteria = taskBuryPointLogCriteria.createCriteria();

        if (id != null) {
            criteria.andIdEqualTo(id);
        }

        if (StringUtils.isNotEmpty(appId)) {
            criteria.andAppIdEqualTo(appId);
        }

        if (taskId != null) {
            criteria.andTaskIdEqualTo(taskId);
        }

        if (StringUtils.isNotEmpty(code)) {
            criteria.andCodeEqualTo(code);
        }

        if (StringUtils.isNotEmpty(order)) {
            taskBuryPointLogCriteria.setOrderByClause(order);
        }

        return taskBuryPointLogMapper.selectByExample(taskBuryPointLogCriteria);
    }

    @Override
    public List<TaskBuryPointLog> listTaskBuryPointLogsDescWithCreateTimeByTaskId(@Nonnull Long taskId) {
        TaskBuryPointLogCriteria taskBuryPointLogCriteria = new TaskBuryPointLogCriteria();
        taskBuryPointLogCriteria.createCriteria().andTaskIdEqualTo(taskId);
        taskBuryPointLogCriteria.setOrderByClause("createTime desc, Id desc");

        return taskBuryPointLogMapper.selectByExample(taskBuryPointLogCriteria);
    }

    @Override
    public void insert(@Nonnull List<TaskBuryPointLog> list) {
        for (TaskBuryPointLog log : list) {
            log.setId(generateUniqueId());
            log.setCreateTime(new Date());
        }

        taskBuryPointLogMapper.batchInsert(list);
    }
}
