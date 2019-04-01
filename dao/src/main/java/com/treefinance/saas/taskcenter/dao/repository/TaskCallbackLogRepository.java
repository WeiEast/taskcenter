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
import com.treefinance.saas.taskcenter.dao.param.TaskCallbackLogQuery;

import javax.annotation.Nonnull;

import java.util.List;

/**
 * @author Jerry
 * @date 2018/11/21 17:00
 */
public interface TaskCallbackLogRepository {

    List<TaskCallbackLog> listTaskCallbackLogsInTaskIds(@Nonnull List<Long> taskIds);

    long countTaskCallbackLogsInTaskIds(@Nonnull List<Long> taskIds);

    List<TaskCallbackLog> listTaskCallbackLogsInTaskIdsWithRowBounds(@Nonnull List<Long> taskIds, int offset, int limit);

    List<TaskCallbackLog> queryTaskCallbackLogs(@Nonnull TaskCallbackLogQuery query);

    void insertOrUpdateLog(Long taskId, Byte type, Long configId, String url, String requestParam, String responseData, int httpCode, String callbackCode, String callbackMsg,
        int consumeTime);

    void insertOrUpdateLog(Long taskId, Long configId, Byte failureReason);
}
