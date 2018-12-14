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

import com.treefinance.saas.taskcenter.dao.entity.Task;
import com.treefinance.saas.taskcenter.dao.entity.TaskAndTaskAttribute;
import com.treefinance.saas.taskcenter.dao.param.TaskAttrCompositeQuery;
import com.treefinance.saas.taskcenter.dao.param.TaskPagingQuery;
import com.treefinance.saas.taskcenter.dao.param.TaskParams;
import com.treefinance.saas.taskcenter.dao.param.TaskQuery;

import javax.annotation.Nonnull;

import java.util.Date;
import java.util.List;

/**
 * @author Jerry
 * @date 2018/11/19 19:54
 */
public interface TaskRepository {

    Task getTaskById(@Nonnull Long id);

    List<Long> listTaskIdsByAppIdAndBizTypeAndUniqueId(@Nonnull String appId, @Nonnull Byte bizType, @Nonnull String uniqueId);

    List<Task> listTasksByStatusAndEnvAndCreateTimeBetween(@Nonnull Byte status, @Nonnull Byte saasEnv, @Nonnull Date startDate, @Nonnull Date endDate);

    @Deprecated
    List<Task> queryPagingTasks(@Nonnull TaskPagingQuery query);

    @Deprecated
    long countPagingTasks(@Nonnull TaskPagingQuery query);

    List<Task> queryTasks(@Nonnull TaskQuery query);

    long countTasks(@Nonnull TaskQuery query);

    List<TaskAndTaskAttribute> queryCompositeTasks(@Nonnull TaskAttrCompositeQuery query);

    long countCompositeTasks(@Nonnull TaskAttrCompositeQuery query);

    Task insertTask(@Nonnull TaskParams taskParams);

    int updateTaskByIdAndStatusNotIn(@Nonnull Task task, @Nonnull Byte... statuses);

    int updateTaskByIdAndStatusNotIn(@Nonnull TaskParams taskParams, @Nonnull Long id, @Nonnull Byte... statuses);

    void updateAccountNoById(@Nonnull Long taskId, @Nonnull String accountNo);

}
