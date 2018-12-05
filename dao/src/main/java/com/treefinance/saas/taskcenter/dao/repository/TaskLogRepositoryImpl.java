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

import com.treefinance.commonservice.uid.UidService;
import com.treefinance.saas.taskcenter.dao.entity.TaskLog;
import com.treefinance.saas.taskcenter.dao.entity.TaskLogCriteria;
import com.treefinance.saas.taskcenter.dao.mapper.TaskLogMapper;
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
 * @date 2018/11/20 23:32
 */
@Repository
public class TaskLogRepositoryImpl implements TaskLogRepository {

    private static final int MAX_ERROR_MSG_LENGTH = 1000;
    @Autowired
    private TaskLogMapper taskLogMapper;
    @Autowired
    private UidService uidService;

    @Override
    public List<TaskLog> listTaskLogsByTaskIdAndMsg(@Nonnull Long taskId, @Nonnull String msg) {
        TaskLogCriteria taskLogCriteria = new TaskLogCriteria();
        taskLogCriteria.createCriteria().andTaskIdEqualTo(taskId).andMsgEqualTo(msg);
        return taskLogMapper.selectByExample(taskLogCriteria);
    }

    @Override
    public List<TaskLog> listTaskLogsDescWithUpdateTimeByTaskId(@Nonnull Long taskId) {
        TaskLogCriteria taskLogCriteria = new TaskLogCriteria();
        taskLogCriteria.setOrderByClause("LastUpdateTime desc");
        taskLogCriteria.createCriteria().andTaskIdEqualTo(taskId);

        return taskLogMapper.selectByExample(taskLogCriteria);
    }

    @Override
    public List<TaskLog> listTaskLogsDescWithOccurTimeInTaskIds(@Nonnull List<Long> taskIds) {
        TaskLogCriteria taskLogCriteria = new TaskLogCriteria();
        taskLogCriteria.createCriteria().andTaskIdIn(taskIds);
        taskLogCriteria.setOrderByClause("OccurTime desc, Id desc");

        return taskLogMapper.selectByExample(taskLogCriteria);
    }

    @Override
    public List<TaskLog> queryTaskLogsByTaskIdAndInMsgs(@Nonnull Long taskId, @Nullable List<String> msgs) {
        TaskLogCriteria taskLogCriteria = new TaskLogCriteria();
        TaskLogCriteria.Criteria criteria = taskLogCriteria.createCriteria().andTaskIdEqualTo(taskId);
        if (CollectionUtils.isNotEmpty(msgs)) {
            criteria.andMsgIn(msgs);
        }

        return taskLogMapper.selectByExample(taskLogCriteria);
    }

    @Override
    public List<TaskLog> queryTaskLogs(Long id, List<Long> taskIds, String msg, String stepCode, String errorMsg, Date occurTime, String order) {
        TaskLogCriteria taskLogCriteria = new TaskLogCriteria();
        TaskLogCriteria.Criteria criteria = taskLogCriteria.createCriteria();
        if (id != null) {
            criteria.andIdEqualTo(id);
        }

        if (CollectionUtils.isNotEmpty(taskIds)) {
            criteria.andTaskIdIn(taskIds);
        }

        if (StringUtils.isNotEmpty(msg)) {
            criteria.andMsgEqualTo(msg);
        }

        if (StringUtils.isNotEmpty(stepCode)) {
            criteria.andStepCodeEqualTo(stepCode);
        }

        if (StringUtils.isNotEmpty(errorMsg)) {
            criteria.andErrorMsgEqualTo(errorMsg);
        }

        if (occurTime != null) {
            criteria.andOccurTimeEqualTo(occurTime);
        }

        if (StringUtils.isNotEmpty(order)) {
            taskLogCriteria.setOrderByClause(order);
        }

        return taskLogMapper.selectByExample(taskLogCriteria);
    }

    @Override
    public TaskLog insertTaskLog(@Nonnull Long taskId, String msg, String stepCode, Date processTime, String errorMsg) {
        TaskLog taskLog = new TaskLog();
        taskLog.setId(uidService.getId());
        taskLog.setTaskId(taskId);
        taskLog.setMsg(msg);
        taskLog.setStepCode(stepCode);
        taskLog.setOccurTime(processTime);
        taskLog.setErrorMsg(limitErrorMsgLength(errorMsg));
        Date now = new Date();
        taskLog.setCreateTime(now);
        taskLog.setLastUpdateTime(now);
        taskLogMapper.insertSelective(taskLog);
        return taskLog;
    }

    private String limitErrorMsgLength(String errorMsg) {
        return errorMsg != null && errorMsg.length() > MAX_ERROR_MSG_LENGTH ? errorMsg.substring(0, MAX_ERROR_MSG_LENGTH) : errorMsg;
    }
}
