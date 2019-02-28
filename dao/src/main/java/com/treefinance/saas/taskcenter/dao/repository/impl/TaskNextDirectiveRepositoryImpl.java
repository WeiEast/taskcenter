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

import com.treefinance.saas.taskcenter.dao.entity.TaskNextDirective;
import com.treefinance.saas.taskcenter.dao.entity.TaskNextDirectiveCriteria;
import com.treefinance.saas.taskcenter.dao.mapper.TaskNextDirectiveMapper;
import com.treefinance.saas.taskcenter.dao.repository.TaskNextDirectiveRepository;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;

import java.util.List;

/**
 * @author Jerry
 * @date 2018/11/21 17:47
 */
@Repository
public class TaskNextDirectiveRepositoryImpl extends AbstractRepository implements TaskNextDirectiveRepository {
    @Autowired
    private TaskNextDirectiveMapper taskNextDirectiveMapper;

    @Override
    public TaskNextDirective getLastDirectiveByTaskId(@Nonnull Long taskId) {
        List<TaskNextDirective> list = listDirectivesDescWithCreateTimeByTaskId(taskId);

        return CollectionUtils.isNotEmpty(list) ? list.get(0) : null;
    }

    @Override
    public List<TaskNextDirective> listDirectivesDescWithCreateTimeByTaskId(@Nonnull Long taskId) {
        TaskNextDirectiveCriteria taskNextDirectiveCriteria = new TaskNextDirectiveCriteria();
        taskNextDirectiveCriteria.createCriteria().andTaskIdEqualTo(taskId);
        taskNextDirectiveCriteria.setOrderByClause("createTime desc,id desc");

        return taskNextDirectiveMapper.selectByExample(taskNextDirectiveCriteria);
    }

    @Override
    public TaskNextDirective insertDirective(@Nonnull Long taskId, String directive, String remark) {
        TaskNextDirective taskNextDirective = new TaskNextDirective();
        taskNextDirective.setId(generateUniqueId());
        taskNextDirective.setTaskId(taskId);
        taskNextDirective.setDirective(directive);
        taskNextDirective.setRemark(remark);

        taskNextDirectiveMapper.insertSelective(taskNextDirective);

        return taskNextDirective;
    }
}
