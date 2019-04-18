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

import com.treefinance.saas.taskcenter.biz.domain.TaskUpdateResult;
import com.treefinance.saas.taskcenter.biz.param.TaskCreateObject;
import com.treefinance.saas.taskcenter.biz.param.TaskUpdateObject;
import com.treefinance.saas.taskcenter.dao.entity.Task;
import com.treefinance.saas.taskcenter.dao.entity.TaskAndTaskAttribute;
import com.treefinance.saas.taskcenter.dao.param.TaskAttrCompositeQuery;
import com.treefinance.saas.taskcenter.dao.param.TaskPagingQuery;
import com.treefinance.saas.taskcenter.dao.param.TaskQuery;
import com.treefinance.saas.taskcenter.dto.TaskDTO;
import com.treefinance.saas.taskcenter.service.param.TaskStepLogObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.Date;
import java.util.List;

/**
 * @author Jerry
 * @date 2018/11/19 19:55
 */
public interface TaskService {

    TaskDTO getById(Long taskId);

    /**
     * 根据任务ID 和额外变量名字取出任务
     * @param taskId
     * @return
     */
    TaskDTO getTaskandAttribute(Long taskId);

    List<TaskAndTaskAttribute> queryCompositeTasks(@Nonnull TaskAttrCompositeQuery query);

    long countCompositeTasks(@Nonnull TaskAttrCompositeQuery query);

    /**
     * 更新未完成任务
     *
     */
    @Deprecated
    int updateUnfinishedTask(Task task);

    /**
     * 更新AccountNo
     *
     * @param taskId
     * @param accountNo
     * @param website
     */
    @Deprecated
    void updateTask(Long taskId, String accountNo, String website);

    /**
     * 根据任务ID获取任务
     * 
     * @param taskId 任务ID
     * @return 任务信息，非空
     */
    Task getTaskById(@Nonnull Long taskId);

    /**
     * 获取任务的状态
     *
     * @param taskId 任务ID
     * @return 任务状态值
     */
    Byte getTaskStatusById(@Nonnull Long taskId);

    /**
     * 任务是否完成
     *
     * @param taskId 任务ID
     * @return true if the task was done, otherwise false.
     */
    boolean isTaskCompleted(Long taskId);

    /**
     * 根据taskId查询任务斌检查是否是完成的。
     *
     * @param taskId 任务ID
     * @return {@link Task} if the task was completed, otherwise null.
     */
    Task queryCompletedTaskById(@Nonnull Long taskId);

    /**
     * 列出跟指定任务具有相同触发条件的任务ID。 触发条件包括相同的appId，相同的uniqueId以及相同的业务类型
     *
     * @param taskId 任务ID
     * @return taskId的列表
     */
    List<Long> listTaskIdsWithSameTrigger(@Nonnull Long taskId);

    /**
     * 列出运行中的任务
     * 
     * @param saasEnv 环境标识
     * @param startDate 起始时间（包含）
     * @param endDate 结束时间（不包含）
     * @return 任务列表
     */
    List<Task> listRunningTasks(@Nonnull Byte saasEnv, @Nonnull Date startDate, @Nonnull Date endDate);

    /**
     * 根据分页查询条件查询任务
     * 
     * @param query 分页查询条件
     * @return 任务列表
     */
    @Deprecated
    List<Task> queryPagingTasks(@Nonnull TaskPagingQuery query);

    @Deprecated
    long countPagingTasks(@Nonnull TaskPagingQuery query);

    /**
     * 根据查询条件查询任务
     *
     * @param query 查询条件, included {@link TaskQuery} or {@link TaskPagingQuery}
     * @return 任务列表
     */
    List<Task> queryTasks(@Nonnull TaskQuery query);

    /**
     * 根据查询条件统计任务数
     * 
     * @param query 查询条件
     * @return 任务数
     */
    long countTasks(@Nonnull TaskQuery query);

    /**
     * 创建任务
     *
     * @param object 任务创建信息
     * @return 任务ID
     */
    Long createTask(@Nonnull TaskCreateObject object);

    /**
     * 根据ID更新处理中的任务信息
     * 
     * @param object 任务更新信息
     * @return 更新的记录数
     */
    int updateProcessingTaskById(@Nonnull TaskUpdateObject object);

    /**
     * 针对正在处理中的任务，根据需要更新任务的账号<code>accountNo</code>和网站标识<code>website</code>
     *
     * @param taskId 任务ID
     * @param accountNo 账号
     * @param website 网站标识
     * @return 更新结果{@link TaskUpdateResult}
     */
    TaskUpdateResult updateAccountNoAndWebsiteIfNeedWhenProcessing(@Nonnull Long taskId, @Nullable String accountNo, @Nullable String website);

    /**
     * 针对正在处理中的任务，更新任务的账号<code>accountNo</code>和网站标识<code>website</code>
     *
     * @param taskId 任务ID
     * @param accountNo 账号
     * @param website 网站标识
     * @return 更新结果{@link TaskUpdateResult}
     */
    TaskUpdateResult updateAccountNoAndWebsiteWhenProcessing(@Nonnull Long taskId, @Nullable String accountNo, @Nullable String website);

    /**
     * 针对正在处理中的任务，更改任务状态
     *
     * @param taskId 任务ID
     * @param status 状态
     */
    void updateStatusWhenProcessing(@Nonnull Long taskId, @Nonnull Byte status);

    /**
     * 更新任务完成时的任务状态，比如：成功，失败或取消
     * 
     * @param taskId 任务ID
     * @param status 任务状态，比如：成功，失败或取消
     * @return stepCode when the task was cancel or failed, otherwise return null.
     */
    String updateStatusIfDone(@Nonnull Long taskId, @Nonnull Byte status);

    /**
     * 更新任务的账号<code>accountNo</code>
     * 
     * @param taskId 任务ID
     * @param accountNo 账号
     */
    void updateAccountNoById(@Nonnull Long taskId, @Nonnull String accountNo);

    /**
     * 正常流程下取消任务
     *
     * @param taskId 任务id
     */
    void cancelTask(@Nonnull Long taskId);

    void completeTaskAndMonitoring(@Nonnull Long taskId, @Nonnull List<TaskStepLogObject> logList);
}
