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

package com.treefinance.saas.taskcenter.biz.service;

import com.treefinance.saas.taskcenter.common.model.dto.TaskDTO;
import com.treefinance.saas.taskcenter.dao.domain.TaskCompositeQuery;
import com.treefinance.saas.taskcenter.dao.domain.TaskDO;
import com.treefinance.saas.taskcenter.dao.domain.TaskQuery;
import com.treefinance.saas.taskcenter.dao.entity.Task;
import com.treefinance.saas.taskcenter.dao.entity.TaskAndTaskAttribute;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.Date;
import java.util.List;

/**
 * @author Jerry
 * @date 2018/11/19 19:55
 */
public interface TaskService {

    Task getTaskById(@Nonnull Long taskId);

    List<Long> getUserTaskIdList(Long taskId);

    TaskDTO getById(Long taskId);

    /**
     * 任务是否完成
     *
     * @param taskId
     * @return
     */
    boolean isTaskCompleted(Long taskId);

    Byte getTaskStatusById(@Nonnull Long taskId);

    List<Task> listRunningTasksByEnvAndCreateTimeBetween(@Nonnull Byte saasEnv, @Nonnull Date startDate,
        @Nonnull Date endDate);

    List<Task> queryTasks(@Nonnull TaskQuery query);

    long countTasks(@Nonnull TaskQuery query);

    List<TaskAndTaskAttribute> queryCompositeTasks(@Nonnull TaskCompositeQuery query);

    long countCompositeTasks(@Nonnull TaskCompositeQuery query);

    /**
     * 创建任务
     *
     */
    Long createTask(@Nonnull TaskDO taskDO, String source, String extra);

    /**
     * 正常流程下取消任务
     *
     * @param taskId 任务id
     */
    void cancelTask(Long taskId);

    /**
     * 更新未完成任务
     *
     */
    int updateUnfinishedTask(Task task);

    void updateStatusInStepById(Long taskId, Byte status, String stepCode);

    /**
     * 更改任务状态
     *
     * @param taskId
     * @param status
     */
    void updateStatusById(@Nonnull Long taskId, @Nonnull Byte status);

    String updateStatusIfDone(Long taskId, Byte status);

    /**
     * 更新AccountNo
     *
     * @param taskId
     * @param accountNo
     * @param website
     */
    void updateTask(Long taskId, String accountNo, String website);

    void updateAccountNoAndWebsiteIfNeed(@Nonnull Long taskId, @Nullable String accountNo, @Nullable String website);

    void updateAccountNoById(@Nonnull Long taskId, @Nonnull String accountNo);
}
