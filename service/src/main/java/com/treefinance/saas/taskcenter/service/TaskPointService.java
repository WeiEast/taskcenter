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

import com.treefinance.saas.taskcenter.service.param.TaskPointCreateObject;

/**
 * @author 张琰佳
 * @since 8:38 PM 2019/1/24
 */
public interface TaskPointService {

    /**
     * 添加系统埋点
     * 
     * @param taskId 任务ID
     * @param pointCode 埋点编号
     */
    void addTaskPoint(Long taskId, String pointCode);

    /**
     * 添加系统埋点
     * 
     * @param taskId 任务ID
     * @param pointCode 埋点编号
     * @param ip IP
     */
    void addTaskPoint(Long taskId, String pointCode, String ip);

    /**
     * 添加埋点
     *
     * @param createObject 埋点参数
     */
    void addTaskPoint(TaskPointCreateObject createObject);
}
