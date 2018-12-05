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

import com.treefinance.saas.taskcenter.common.model.dto.DirectiveDTO;
import com.treefinance.saas.taskcenter.dao.entity.TaskNextDirective;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.List;

/**
 * @author Jerry
 * @date 2018/11/21 17:50
 */
public interface TaskNextDirectiveService {

    /**
     * 查询最近一条指令记录
     *
     * @param taskId 任务ID
     * @return the latest {@link TaskNextDirective}
     */
    TaskNextDirective getLastDirectiveByTaskId(@Nonnull Long taskId);

    List<TaskNextDirective> listDirectivesDescWithCreateTimeByTaskId(@Nonnull Long taskId);

    /**
     * 添加一条指令记录
     *
     * @param taskId 任务ID
     * @param directive 指令
     * @param remark 备注信息
     * @return the id of {@link TaskNextDirective}
     */
    Long insert(Long taskId, String directive, String remark);

    /**
     * 添加一条指令记录
     *
     * @param taskId 任务ID
     * @param directive 指令
     * @return the id of {@link TaskNextDirective}
     * @see #insert(Long, String, String)
     */
    default Long insert(Long taskId, String directive) {
        return this.insert(taskId, directive, null);
    }

    /**
     * 记录并缓存指令
     *
     *
     * @param taskId 任务ID
     * @param directive 指令对象
     */
    void insertAndCacheNextDirective(@Nonnull Long taskId, @Nonnull DirectiveDTO directive);

    /**
     * 获取指令
     *
     * @param taskId 任务ID
     * @return 指令
     */
    String getNextDirective(@Nonnull Long taskId);

    /**
     * 删除指令。注意：数据库中的指令是只插入的,所以这里的删除指插入waiting指令
     *
     * @param taskId 任务ID
     */
    void deleteNextDirective(@Nonnull Long taskId);

    void deleteNextDirective(@Nonnull Long taskId, @Nullable String directive);
}
