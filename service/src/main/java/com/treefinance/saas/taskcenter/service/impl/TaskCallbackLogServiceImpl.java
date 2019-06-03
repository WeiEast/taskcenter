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

package com.treefinance.saas.taskcenter.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.treefinance.saas.taskcenter.context.Constants;
import com.treefinance.saas.taskcenter.dao.entity.TaskCallbackLog;
import com.treefinance.saas.taskcenter.dao.param.TaskCallbackLogQuery;
import com.treefinance.saas.taskcenter.dao.repository.TaskCallbackLogRepository;
import com.treefinance.saas.taskcenter.interation.manager.domain.CallbackConfigBO;
import com.treefinance.saas.taskcenter.service.TaskCallbackLogService;
import com.treefinance.saas.taskcenter.service.param.CallbackRecordObject;
import com.treefinance.saas.taskcenter.service.param.CallbackRecordObject.CallbackResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * Created by haojiahong on 2017/8/17.
 */
@Service
public class TaskCallbackLogServiceImpl implements TaskCallbackLogService {

    private static final Logger logger = LoggerFactory.getLogger(TaskCallbackLogServiceImpl.class);
    private static final int CALLBACK_STRING_LENGTH_LIMIT = 1000;

    @Autowired
    private TaskCallbackLogRepository taskCallbackLogRepository;

    @Override
    public List<TaskCallbackLog> listTaskCallbackLogsInTaskIds(@Nonnull List<Long> taskIds) {
        return taskCallbackLogRepository.listTaskCallbackLogsInTaskIds(taskIds);
    }

    @Override
    public long countTaskCallbackLogsInTaskIds(@Nonnull List<Long> taskIds) {
        return taskCallbackLogRepository.countTaskCallbackLogsInTaskIds(taskIds);
    }

    @Override
    public List<TaskCallbackLog> listTaskCallbackLogsInTaskIdsWithRowBounds(@Nonnull List<Long> taskIds, int offset, int limit) {
        return taskCallbackLogRepository.listTaskCallbackLogsInTaskIdsWithRowBounds(taskIds, offset, limit);
    }

    @Override
    public List<TaskCallbackLog> queryTaskCallbackLogsByTaskIdAndInConfigIds(@Nonnull Long taskId, @Nullable List<Long> configIds) {
        TaskCallbackLogQuery query = new TaskCallbackLogQuery();
        query.setTaskIds(Collections.singletonList(taskId));
        query.setConfigIds(configIds);

        return taskCallbackLogRepository.queryTaskCallbackLogs(query);
    }

    @Override
    public List<TaskCallbackLog> queryTaskCallbackLogs(@Nonnull TaskCallbackLogQuery query) {
        return taskCallbackLogRepository.queryTaskCallbackLogs(query);
    }

    @Override
    public void insert(@Nonnull Long taskId, @Nonnull CallbackRecordObject record) {
        try {
            final Byte type = record.getType();

            String requestParam = StringUtils.trimToEmpty(record.getRequestParameters());
            if (StringUtils.isNotEmpty(requestParam) && requestParam.length() > CALLBACK_STRING_LENGTH_LIMIT) {
                requestParam = requestParam.substring(0, CALLBACK_STRING_LENGTH_LIMIT);
            }

            Long configId = 0L;
            String url = null;
            String responseData = null;
            String callbackCode = null;
            String callbackMsg = null;
            int httpCode = 0;
            if (Constants.CALLBACK_TYPE_BACKEND.equals(type)) {
                final CallbackConfigBO config = record.getConfig();
                if (config != null) {
                    configId = Long.valueOf(config.getId());
                    url = config.getUrl();
                }

                responseData = StringUtils.trimToEmpty(record.getResponseData());
                if (responseData.length() > CALLBACK_STRING_LENGTH_LIMIT) {
                    responseData = responseData.substring(0, CALLBACK_STRING_LENGTH_LIMIT) + "...";
                }

                if (record.getResponseStatusCode() == HttpStatus.SC_OK) {
                    callbackMsg = "回调成功";
                } else if (record.getResponseStatusCode() == 0) {
                    final Throwable exception = record.getException();
                    callbackMsg = exception == null ? "回调执行错误" : "回调执行失败：" + exception.getMessage();
                } else {
                    final CallbackResult callbackResult = record.getCallbackResult();
                    if (callbackResult != null) {
                        callbackCode = callbackResult.getCode();
                        callbackMsg = callbackResult.getErrorMsg();
                        if (StringUtils.isBlank(callbackMsg)) {
                            callbackMsg = "回调错误信息为空";
                        }
                    }
                }

                httpCode = record.getResponseStatusCode();
            }

            final long consumeTime = record.getCost();
            taskCallbackLogRepository.insertOrUpdateLog(taskId, type, configId, url, requestParam, responseData, httpCode, callbackCode, callbackMsg, (int)consumeTime);
        } catch (Exception e) {
            logger.warn("保存回调操作日志发生错误！- taskId：{}, callbackRecord: {}", taskId, record, e);
        }
    }

    @Override
    public void insert(CallbackConfigBO config, Long taskId, Byte type, String params, String result, long consumeTime, int httpCode) {
        Long configId = 0L;
        String url = null;
        if (config != null) {
            configId = Long.valueOf(config.getId());
            url = config.getUrl();
        }
        String requestParam = StringUtils.trimToEmpty(params);
        if (StringUtils.isNotEmpty(requestParam) && requestParam.length() > 1000) {
            requestParam = requestParam.substring(0, 1000);
        }

        String responseData = StringUtils.trimToEmpty(result);
        String callbackCode = null;
        String callbackMsg = null;
        if (StringUtils.isNotEmpty(responseData)) {
            if (httpCode == 200) {
                callbackMsg = "回调成功";
            } else {
                try {
                    JSONObject jsonObject = JSON.parseObject(responseData);
                    callbackCode = jsonObject.getString("code");
                    callbackMsg = jsonObject.getString("errorMsg");
                    if (StringUtils.isBlank(callbackMsg)) {
                        callbackMsg = "回调错误信息为空";
                    }
                } catch (Exception e) {
                    logger.error("记录回调错误信息:解析返回回调结果json有误,taskId={},回调返回结果result={}", taskId, responseData);
                    callbackMsg = responseData.length() > 1000 ? responseData.substring(0, 100) + "..." : responseData;
                }
            }

            if (responseData.length() > 1000) {
                responseData = responseData.substring(0, 1000);
            }
        }

        taskCallbackLogRepository.insertOrUpdateLog(taskId, type, configId, url, requestParam, responseData, httpCode, callbackCode, callbackMsg, (int)consumeTime);
    }

}
