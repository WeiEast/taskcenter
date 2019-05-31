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

import com.treefinance.saas.taskcenter.dao.entity.TaskAttribute;
import com.treefinance.saas.taskcenter.dao.param.TaskAttributeQuery;
import com.treefinance.toolkit.util.DateUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Jerry
 * @date 2018/11/20 20:49
 */
public interface TaskAttributeRepository {

    Map<String, String> getAttributeMapByTaskId(@Nonnull Long taskId, boolean decrypt);

    Map<String, String> getAttributeMapByTaskIdAndInNames(@Nonnull Long taskId, @Nonnull List<String> names, boolean decrypt);

    TaskAttribute queryAttributeByTaskIdAndName(@Nonnull Long taskId, @Nonnull String name, boolean decrypt);

    TaskAttribute queryAttributeByTaskIdAndName(@Nonnull Long taskId, @Nonnull String name);

    TaskAttribute queryAttributeByNameAndValue(@Nonnull String name, @Nonnull String value, boolean encrypt);

    List<TaskAttribute> listAttributesByNameAndValue(@Nonnull String name, @Nonnull String value, boolean encrypt);

    /**
     * 根据taskId查询所有属性
     *
     * @param taskId 任务ID
     * @return a list of task's attributes
     */
    List<TaskAttribute> listAttributesByTaskId(@Nonnull Long taskId);

    List<TaskAttribute> listAttributesByTaskIdAndName(@Nonnull Long taskId, @Nonnull String name);

    List<TaskAttribute> listAttributesByTaskIdAndInNames(@Nonnull Long taskId, @Nonnull List<String> names);

    List<TaskAttribute> listAttributesByTaskIdAndInNames(@Nonnull Long taskId, @Nonnull List<String> names, boolean decrypt);

    List<TaskAttribute> listAttributesInTaskIdsAndByName(@Nonnull List<Long> taskIds, @Nonnull String name);

    List<TaskAttribute> listAttributesInTaskIdsAndByName(@Nonnull List<Long> taskIds, @Nonnull String name,
        boolean decrypt);

    List<TaskAttribute> queryAttributes(@Nonnull TaskAttributeQuery query);

    Long insertAttribute(@Nonnull Long taskId, @Nonnull String name, @Nullable String value, boolean encrypt);

    void insertOrUpdateAttribute(@Nonnull Long taskId, @Nonnull String name, @Nullable String value, boolean encrypt);

    /**
     * 保存或更新属性,默认不加密属性值
     *
     * @param taskId 任务ID
     * @param name 属性名
     * @param value 属性值
     * @see #insertOrUpdateAttribute(Long, String, String, boolean)
     */
    default void insertOrUpdateAttribute(@Nonnull Long taskId, @Nonnull String name, String value) {
        this.insertOrUpdateAttribute(taskId, name, value, false);
    }

    /**
     * 保存或更新属性,默认不加密属性值
     *
     * @param taskId 任务ID
     * @param name 属性名
     * @param date 属性值，时间类型
     * @see #insertOrUpdateAttribute(Long, String, String)
     */
    default void insertOrUpdateAttribute(@Nonnull Long taskId, @Nonnull String name, Date date) {
        this.insertOrUpdateAttribute(taskId, name, DateUtils.format(date));
    }

    /**
     * 根据任务id和name删除属性
     *
     * @param taskId 任务ID
     * @param name 属性名
     * @return the number of records that has been deleted
     */
    int deleteAttributeByTaskIdAndName(@Nonnull Long taskId, @Nonnull String name);

    default Date queryAttributeValueAsDate(@Nonnull Long taskId, @Nonnull String name) {
        TaskAttribute taskAttribute = this.queryAttributeByTaskIdAndName(taskId, name);
        if (taskAttribute != null) {
            return DateUtils.parse(taskAttribute.getValue());
        }
        return null;
    }
}
