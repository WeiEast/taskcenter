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

import com.treefinance.saas.taskcenter.common.enums.ETaskStep;
import com.treefinance.saas.taskcenter.context.enums.TaskStatusMsgEnum;
import com.treefinance.saas.taskcenter.dao.entity.TaskLog;

import javax.annotation.Nonnull;

import java.util.Date;
import java.util.List;

/**
 * @author Jerry
 * @date 2018/11/20 16:52
 */
public interface TaskLogService {
    /**
     * 根据任务ID查询最后一个错误日志
     *
     * @param taskId 任务ID
     * @return log
     */
    TaskLog queryLastErrorLog(Long taskId);

    /**
     * 根据任务ID查询最后一个错误日志对应的步骤编码
     *
     * @param taskId 任务ID
     * @return StepCode
     */
    String getLastErrorStepCode(Long taskId);

    List<TaskLog> queryTaskLogs(Long id, List<Long> taskIds, String msg, String stepCode, String errorMsg, Date occurTime, String order);

    /**
     *
     * 添加一条日志记录
     *
     * @param taskId 任务ID
     * @param msg 任务信息
     * @param processTime 处理时间
     * @param errorMsg 错误信息
     * @return 日志记录ID
     */
    Long insertTaskLog(Long taskId, String msg, Date processTime, String errorMsg);

    List<TaskLog> listTaskLogsByTaskIdAndMsg(Long taskId, String msg);

    List<TaskLog> listTaskLogsDescWithOccurTimeInTaskIds(@Nonnull List<Long> taskIds);

    List<TaskLog> queryTaskLogsByTaskIdAndInSteps(@Nonnull Long taskId, ETaskStep... steps);

    Long log(Long taskId, TaskStatusMsgEnum msgEnum);

    Long log(Long taskId, TaskStatusMsgEnum msgEnum, String errorMsg);
}
