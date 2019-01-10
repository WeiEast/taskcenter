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

import com.treefinance.saas.taskcenter.dao.entity.TaskCallbackLog;
import com.treefinance.saas.taskcenter.dao.entity.TaskCallbackLogCriteria;
import com.treefinance.saas.taskcenter.dao.mapper.TaskCallbackLogMapper;
import com.treefinance.saas.taskcenter.dao.mapper.TaskCallbackLogUpdateMapper;
import com.treefinance.saas.taskcenter.dao.param.TaskCallbackLogQuery;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;

import java.util.List;

/**
 * @author Jerry
 * @date 2018/11/21 17:01
 */
@Repository
public class TaskCallbackLogRepositoryImpl extends AbstractRepository implements TaskCallbackLogRepository {

    @Autowired
    private TaskCallbackLogMapper taskCallbackLogMapper;
    @Autowired
    private TaskCallbackLogUpdateMapper taskCallbackLogUpdateMapper;

    @Override
    public List<TaskCallbackLog> listTaskCallbackLogsInTaskIds(@Nonnull List<Long> taskIds) {
        TaskCallbackLogCriteria taskCallbackLogCriteria = new TaskCallbackLogCriteria();
        taskCallbackLogCriteria.createCriteria().andTaskIdIn(taskIds);

        return taskCallbackLogMapper.selectByExample(taskCallbackLogCriteria);
    }

    @Override
    public long countTaskCallbackLogsInTaskIds(@Nonnull List<Long> taskIds) {
        TaskCallbackLogCriteria taskCallbackLogCriteria = new TaskCallbackLogCriteria();
        taskCallbackLogCriteria.createCriteria().andTaskIdIn(taskIds);

        return taskCallbackLogMapper.countByExample(taskCallbackLogCriteria);
    }

    @Override
    public List<TaskCallbackLog> listTaskCallbackLogsInTaskIdsWithRowBounds(@Nonnull List<Long> taskIds, int offset, int limit) {
        TaskCallbackLogCriteria taskCallbackLogCriteria = new TaskCallbackLogCriteria();
        taskCallbackLogCriteria.createCriteria().andTaskIdIn(taskIds);
        taskCallbackLogCriteria.setOffset(offset);
        taskCallbackLogCriteria.setLimit(limit);

        return taskCallbackLogMapper.selectPaginationByExample(taskCallbackLogCriteria);
    }

    @Override
    public List<TaskCallbackLog> queryTaskCallbackLogs(@Nonnull TaskCallbackLogQuery query) {
        TaskCallbackLogCriteria taskCallbackLogCriteria = new TaskCallbackLogCriteria();
        TaskCallbackLogCriteria.Criteria criteria = taskCallbackLogCriteria.createCriteria();

        Long id = query.getId();
        if (id != null) {
            criteria.andIdEqualTo(id);
        }

        List<Long> taskIds = query.getTaskIds();
        if (CollectionUtils.isNotEmpty(taskIds)) {
            if (taskIds.size() == 1) {
                criteria.andTaskIdEqualTo(taskIds.get(0));
            } else {
                criteria.andTaskIdIn(taskIds);
            }
        }

        List<Long> configIds = query.getConfigIds();
        if (CollectionUtils.isNotEmpty(configIds)) {
            if (configIds.size() == 1) {
                criteria.andConfigIdEqualTo(configIds.get(0));
            } else {
                criteria.andConfigIdIn(configIds);
            }
        }

        Byte type = query.getType();
        if (type != null) {
            criteria.andTypeEqualTo(type);
        }

        String url = query.getUrl();
        if (StringUtils.isNotEmpty(url)) {
            criteria.andUrlEqualTo(url);
        }

        String requestParam = query.getRequestParam();
        if (StringUtils.isNotEmpty(requestParam)) {
            criteria.andRequestParamEqualTo(requestParam);
        }
        String responseData = query.getResponseData();
        if (StringUtils.isNotEmpty(responseData)) {
            criteria.andResponseDataEqualTo(responseData);
        }

        Integer httpCode = query.getHttpCode();
        if (httpCode != null) {
            criteria.andHttpCodeEqualTo(httpCode);
        }

        Integer consumeTime = query.getConsumeTime();
        if (consumeTime != null) {
            criteria.andConsumeTimeEqualTo(consumeTime);
        }

        String callbackCode = query.getCallbackCode();
        if (StringUtils.isNotEmpty(callbackCode)) {
            criteria.andCallbackCodeEqualTo(callbackCode);
        }

        String callbackMsg = query.getCallbackMsg();
        if (StringUtils.isNotEmpty(callbackMsg)) {
            criteria.andCallbackMsgEqualTo(callbackMsg);
        }

        Byte failureReason = query.getFailureReason();
        if (failureReason != null) {
            criteria.andFailureReasonEqualTo(failureReason);
        }

        return taskCallbackLogMapper.selectByExample(taskCallbackLogCriteria);
    }

    @Override
    public void insertOrUpdateLog(Long taskId, Byte type, Long configId, String url, String requestParam, String responseData, int httpCode, String callbackCode,
        String callbackMsg, int consumeTime) {
        TaskCallbackLog taskCallbackLog = new TaskCallbackLog();
        taskCallbackLog.setId(generateUniqueId());
        taskCallbackLog.setTaskId(taskId);
        taskCallbackLog.setUrl(url);
        taskCallbackLog.setConfigId(configId);
        taskCallbackLog.setType(type);
        taskCallbackLog.setRequestParam(requestParam);
        taskCallbackLog.setCallbackCode(callbackCode);
        taskCallbackLog.setCallbackMsg(callbackMsg);
        taskCallbackLog.setResponseData(responseData);
        taskCallbackLog.setHttpCode(httpCode);
        taskCallbackLog.setConsumeTime(consumeTime);

        taskCallbackLogUpdateMapper.insertOrUpdateSelective(taskCallbackLog);
    }

    @Override
    public void insertOrUpdateLog(Long taskId, Long configId, Byte failureReason) {
        TaskCallbackLog taskCallbackLog = new TaskCallbackLog();
        taskCallbackLog.setId(generateUniqueId());
        taskCallbackLog.setTaskId(taskId);
        taskCallbackLog.setConfigId(configId);
        taskCallbackLog.setFailureReason(failureReason);
        taskCallbackLogUpdateMapper.insertOrUpdateSelective(taskCallbackLog);
    }
}
