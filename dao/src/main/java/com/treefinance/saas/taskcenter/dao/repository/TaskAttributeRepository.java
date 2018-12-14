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

import com.treefinance.saas.taskcenter.dao.param.TaskAttributeQuery;
import com.treefinance.saas.taskcenter.dao.entity.TaskAttribute;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.List;
import java.util.Map;

/**
 * @author Jerry
 * @date 2018/11/20 20:49
 */
public interface TaskAttributeRepository {

    Map<String, String> getAttributeMapByTaskIdAndInNames(@Nonnull Long taskId, @Nonnull List<String> names, boolean decrypt);

    TaskAttribute getTaskAttributeByTaskIdAndName(@Nonnull Long taskId, @Nonnull String name, boolean decrypt);

    TaskAttribute getTaskAttributeByTaskIdAndName(@Nonnull Long taskId, @Nonnull String name);

    TaskAttribute getTaskAttributeByNameAndValue(@Nonnull String name, @Nonnull String value, boolean encrypt);

    List<TaskAttribute> listTaskAttributesByNameAndValue(@Nonnull String name, @Nonnull String value, boolean encrypt);

    /**
     * 根据taskId查询所有属性
     *
     * @param taskId 任务ID
     * @return a list of task's attributes
     */
    List<TaskAttribute> listTaskAttributesByTaskId(@Nonnull Long taskId);

    List<TaskAttribute> listTaskAttributesByTaskIdAndName(@Nonnull Long taskId, @Nonnull String name);

    List<TaskAttribute> listTaskAttributesByTaskIdAndInNames(@Nonnull Long taskId, @Nonnull List<String> names);

    List<TaskAttribute> listTaskAttributesByTaskIdAndInNames(@Nonnull Long taskId, @Nonnull List<String> names, boolean decrypt);

    List<TaskAttribute> listTaskAttributesByNameAndInTaskIds(@Nonnull String name, @Nonnull List<Long> taskIds);

    List<TaskAttribute> queryTaskAttributes(@Nonnull TaskAttributeQuery query);

    Long insertTagAttribute(@Nonnull Long taskId, @Nonnull String name, @Nullable String value, boolean encrypt);

    void insertOrUpdateTagAttribute(@Nonnull Long taskId, @Nonnull String name, @Nullable String value, boolean encrypt);

    /**
     * 根据任务id和name删除属性
     *
     * @param taskId 任务ID
     * @param name 属性名
     * @return the number of records that has been deleted
     */
    int deleteByTaskIdAndName(@Nonnull Long taskId, @Nonnull String name);
}
