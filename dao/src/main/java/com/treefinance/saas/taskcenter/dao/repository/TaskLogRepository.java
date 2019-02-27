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

import com.treefinance.saas.taskcenter.dao.entity.TaskLog;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.Date;
import java.util.List;

/**
 * @author Jerry
 * @date 2018/11/20 23:32
 */
public interface TaskLogRepository {

    List<TaskLog> listTaskLogsByTaskIdAndMsg(@Nonnull Long taskId, @Nonnull String msg);

    List<TaskLog> listTaskLogsDescWithUpdateTimeByTaskId(@Nonnull Long taskId);

    List<TaskLog> listTaskLogsDescWithOccurTimeInTaskIds(@Nonnull List<Long> taskIds);

    List<TaskLog> queryTaskLogsByTaskIdAndInMsgs(@Nonnull Long taskId, @Nullable List<String> msgs);

    List<TaskLog> queryTaskLogs(Long id, List<Long> taskIds, String msg, String stepCode, String errorMsg, Date occurTime, String order);

    /**
     * 添加一条日志记录
     *
     * @param taskId 任务ID
     * @param msg 任务信息
     * @param stepCode 步骤
     * @param processTime 处理时间
     * @param errorMsg 错误信息
     * @return {@link TaskLog}
     */
    TaskLog insertTaskLog(@Nonnull Long taskId, String msg, String stepCode, Date processTime, String errorMsg);
}
