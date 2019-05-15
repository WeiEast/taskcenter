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

import com.treefinance.saas.taskcenter.dao.entity.TaskBuryPointLog;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.Arrays;
import java.util.List;

/**
 * @author Jerry
 * @date 2018/11/21 16:27
 */
public interface TaskBuryPointRepository {

    List<TaskBuryPointLog> queryTaskBuryPointLogsByTaskIdAndInCodes(@Nonnull Long taskId, @Nullable List<String> codes);

    long countTaskBuryPointLogsByTaskIdAndInCodes(@Nonnull Long taskId, @Nullable List<String> codes);

    default boolean doesAnyExist(@Nonnull Long taskId, @Nonnull String... codes) {
        return countTaskBuryPointLogsByTaskIdAndInCodes(taskId, Arrays.asList(codes)) > 0;
    }

    List<TaskBuryPointLog> queryTaskBuryPointLogs(Long id, String appId, Long taskId, String code, String order);

    List<TaskBuryPointLog> listTaskBuryPointLogsDescWithCreateTimeByTaskId(@Nonnull Long taskId);

    void insert(@Nonnull List<TaskBuryPointLog> list);
}
