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

package com.treefinance.saas.taskcenter.service;

import java.util.Date;

/**
 * @author Jerry
 * @date 2018/11/21 16:15
 */
public interface TaskLifecycleService {

    /**
     * 获取活跃时间
     *
     * @param taskId 任务ID
     * @return 活跃时间字符
     */
    String queryAliveTime(Long taskId);

    /**
     * 获取活跃时间
     *
     * @param taskId 任务ID
     * @return 活跃时间的时间戳
     */
    Long queryAliveTimeInMills(Long taskId);

    /**
     * 更新任务的活跃时间 可能存在多个请求同时更新活跃时间,未获得锁的请求可过滤掉
     *
     * @param taskId 任务ID
     * @param date 活跃时间
     */
    void updateAliveTime(Long taskId, Date date);

    /**
     * 设置活跃时间
     * 
     * @param taskId 任务ID
     */
    default void updateAliveTime(Long taskId) {
        this.updateAliveTime(taskId, new Date());
    }

    /**
     * 删除活跃时间
     * 
     * @param taskId 任务ID
     */
    void deleteAliveTime(Long taskId);

    /**
     * 获取设置的任务超时时长
     *
     * @param taskId 任务ID
     * @return 任务超时时长，单位：秒
     */
    Integer getTimeoutInSeconds(Long taskId);
}
