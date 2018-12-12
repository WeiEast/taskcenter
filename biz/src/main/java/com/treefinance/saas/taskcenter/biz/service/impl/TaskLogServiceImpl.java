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

import com.treefinance.saas.taskcenter.biz.service.TaskAliveService;
import com.treefinance.saas.taskcenter.biz.service.TaskLogService;
import com.treefinance.saas.taskcenter.biz.service.monitor.TaskRealTimeStatMonitorService;
import com.treefinance.saas.taskcenter.context.enums.ETaskStep;
import com.treefinance.saas.taskcenter.context.enums.TaskStatusMsgEnum;
import com.treefinance.saas.taskcenter.dao.entity.TaskLog;
import com.treefinance.saas.taskcenter.dao.repository.TaskLogRepository;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by luoyihua on 2017/4/26.
 */
@Service
public class TaskLogServiceImpl implements TaskLogService {

    /**
     * logger
     */
    private static final Logger logger = LoggerFactory.getLogger(TaskLogServiceImpl.class);

    @Autowired
    private TaskAliveService taskAliveService;
    @Autowired
    private TaskRealTimeStatMonitorService taskRealTimeStatMonitorService;
    @Autowired
    private TaskLogRepository taskLogRepository;

    @Override
    public TaskLog queryLastErrorLog(@Nullable Long taskId) {
        if (taskId != null) {
            List<TaskLog> taskLogs = taskLogRepository.listTaskLogsDescWithUpdateTimeByTaskId(taskId);
            if (CollectionUtils.isNotEmpty(taskLogs)) {
                return taskLogs.stream().filter(log -> !log.getMsg().contains(TaskStatusMsgEnum.CANCEL_MSG.getText()) && !log.getMsg().contains("成功")).findFirst().orElse(null);
            }
        }

        return null;
    }

    @Override
    public String getLastErrorStepCode(Long taskId) {
        TaskLog taskLog = queryLastErrorLog(taskId);
        if (taskLog != null) {
            return taskLog.getStepCode();
        }
        logger.error("未查询到失败任务日志信息 taskId={}", taskId);
        return null;
    }

    @Override
    public List<TaskLog> listTaskLogsByTaskIdAndMsg(Long taskId, String msg) {
        return taskLogRepository.listTaskLogsByTaskIdAndMsg(taskId, msg);
    }

    @Override
    public List<TaskLog> listTaskLogsDescWithOccurTimeInTaskIds(@Nonnull List<Long> taskIds) {
        return taskLogRepository.listTaskLogsDescWithOccurTimeInTaskIds(taskIds);
    }

    @Override
    public List<TaskLog> queryTaskLogsByTaskIdAndInSteps(@Nonnull Long taskId, ETaskStep... steps) {
        List<String> msgs = null;
        if (ArrayUtils.isNotEmpty(steps)) {
            msgs = Arrays.stream(steps).map(ETaskStep::getText).collect(Collectors.toList());
        }

        return taskLogRepository.queryTaskLogsByTaskIdAndInMsgs(taskId, msgs);
    }

    @Override
    public List<TaskLog> queryTaskLogs(Long id, List<Long> taskIds, String msg, String stepCode, String errorMsg, Date occurTime, String order) {
        return taskLogRepository.queryTaskLogs(id, taskIds, msg, stepCode, errorMsg, occurTime, order);
    }

    @Override
    public Long insertTaskLog(Long taskId, String msg, Date processTime, String errorMsg) {
        String stepCode = ETaskStep.getStepCodeByText(msg);
        TaskLog taskLog = taskLogRepository.insertTaskLog(taskId, msg, stepCode, processTime, errorMsg);

        taskAliveService.updateTaskActiveTime(taskId);

        taskRealTimeStatMonitorService.handleTaskLog(taskId, msg, taskLog.getCreateTime());
        logger.info("记录任务日志: {}", taskLog);
        return taskLog.getId();
    }

    @Override
    public Long log(Long taskId, TaskStatusMsgEnum msgEnum) {
        return insertTaskLog(taskId, msgEnum.getText(), new Date(), null);
    }

    @Override
    public Long log(Long taskId, TaskStatusMsgEnum msgEnum, String errorMsg) {
        return insertTaskLog(taskId, msgEnum.getText(), new Date(), errorMsg);
    }

}