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

package com.treefinance.saas.taskcenter.interation.manager;

import javax.annotation.Nonnull;

import java.util.Map;

/**
 * @author Jerry
 * @date 2019-02-28 20:58
 */
public interface SpiderTaskManager {

    /**
     * 获取任务的 AccountNo
     * 
     * @param taskId 任务ID
     */
    String getAccountNo(@Nonnull Long taskId);

    /**
     * 取消任务
     * 
     * @param taskId 网关任务id
     * @param extra 附加信息,目前null
     */
    void cancel(@Nonnull Long taskId, Map<String, String> extra);

    /**
     * 取消任务
     * 
     * @param taskId 网关任务id
     * @param extra 附加信息,目前null
     */
    void cancelQuietly(@Nonnull Long taskId, Map<String, String> extra);

}
