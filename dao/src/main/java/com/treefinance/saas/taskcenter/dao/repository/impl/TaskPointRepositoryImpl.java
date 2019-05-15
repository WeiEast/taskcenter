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

import com.treefinance.saas.taskcenter.dao.entity.TaskPoint;
import com.treefinance.saas.taskcenter.dao.mapper.TaskPointMapper;
import com.treefinance.saas.taskcenter.dao.param.TaskPointInsertParams;
import com.treefinance.saas.taskcenter.dao.repository.TaskPointRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;

/**
 * @author Jerry
 * @date 2019-05-15 16:12
 */
@Repository
public class TaskPointRepositoryImpl extends AbstractRepository implements TaskPointRepository {

    @Autowired
    private TaskPointMapper taskPointMapper;

    @Override
    public TaskPoint insert(@Nonnull TaskPointInsertParams params) {
        TaskPoint taskPoint = new TaskPoint();
        BeanUtils.copyProperties(params, taskPoint);
        taskPoint.setId(generateUniqueId());

        if (taskPointMapper.insertSelective(taskPoint) == 0) {
            throw new IllegalStateException("保存任务埋点数据失败！");
        }

        return taskPoint;
    }
}
