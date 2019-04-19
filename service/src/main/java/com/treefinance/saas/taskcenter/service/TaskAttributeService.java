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

package com.treefinance.saas.taskcenter.service;

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
 * @date 2018/11/20 20:54
 */
public interface TaskAttributeService {

    /**
     * 通过属性名查询属性值
     *
     * @param taskId 任务ID
     * @param name 属性名
     * @param decrypt 是否要解密，true:是，false:否
     * @return {@link TaskAttribute}
     */
    TaskAttribute queryAttributeByTaskIdAndName(Long taskId, String name, boolean decrypt);

    /**
     * 通过属性名和属性值查询taskId
     *
     * @param name
     * @param value
     * @param encrypt
     * @return
     */
    TaskAttribute queryAttributeByNameAndValue(String name, String value, boolean encrypt);

    Map<String, String> getAttributeMapByTaskId(@Nonnull Long taskId, boolean decrypt);

    Map<String, String> getAttributeMapByTaskIdAndInNames(@Nonnull Long taskId, @Nonnull String[] names, boolean decrypt);

    /**
     * 根据taskId查询所有属性
     *
     * @param taskId 任务ID
     * @return a list of task's attributes
     */
    List<TaskAttribute> listAttributesByTaskId(Long taskId);

    List<TaskAttribute> listAttributesByTaskIdAndInNames(@Nonnull Long taskId, @Nonnull String[] names, boolean decrypt);

    List<TaskAttribute> listAttributesInTaskIdsAndByName(@Nonnull List<Long> taskIds, @Nonnull String name);

    List<TaskAttribute> queryAttributes(@Nonnull TaskAttributeQuery query);

    /**
     * 保存属性
     *
     * @param taskId 任务ID
     * @param name 属性名
     * @param value 属性值
     * @param encrypt 是否加密属性值
     * @return primary key of {@link com.treefinance.saas.taskcenter.dao.entity.TaskAttribute}
     */
    Long insert(@Nonnull Long taskId, @Nonnull String name, @Nullable String value, boolean encrypt);

    /**
     * 保存或更新属性
     *
     * @param taskId 任务ID
     * @param name 属性名
     * @param value 属性值
     * @param encrypt 是否加密属性值
     */
    void insertOrUpdate(@Nonnull Long taskId, @Nonnull String name, @Nullable String value, boolean encrypt);

    /**
     * 保存或更新属性,默认不加密属性值
     *
     * @param taskId 任务ID
     * @param name 属性名
     * @param value 属性值
     * @see #insertOrUpdate(Long, String, String, boolean)
     */
    default void insertOrUpdate(@Nonnull Long taskId, @Nonnull String name, String value) {
        this.insertOrUpdate(taskId, name, value, false);
    }

    default void insertOrUpdate(@Nonnull Long taskId, @Nonnull String name, Date date) {
        this.insertOrUpdate(taskId, name, DateUtils.format(date));
    }

    /**
     * 根据任务id和name删除属性
     *
     * @param taskId 任务ID
     * @param name 属性名
     */
    void deleteAttributeByTaskIdAndName(Long taskId, String name);

    /**
     * 查询登录时间
     *
     * @param taskId 任务ID
     * @return 登录时间
     */
    Date queryLoginTime(@Nonnull Long taskId);

    /**
     * 保存登录时间
     *
     * @param taskId 任务ID
     * @param date 登录时间
     */
    void saveLoginTime(@Nonnull Long taskId, @Nonnull Date date);

    /**
     * 根据魔蝎的任务ID查找实际任务ID
     * @param moxieTaskId 魔蝎任务ID
     * @return 实际任务ID
     */
    Long findTaskIdByMoxieTid(String moxieTaskId);
}
