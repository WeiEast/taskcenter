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

package com.treefinance.saas.taskcenter.biz.service.monitor;

import com.treefinance.saas.taskcenter.common.enums.ETaskStep;

import java.util.Date;

/**
 * @author Jerry
 * @date 2019-04-18 18:07
 */
public interface TaskRealTimeStatMonitor {

    /**
     * 发送任务实时监控消息
     *
     * @param taskId 任务ID
     * @param code {@link ETaskStep#getText()}
     * @param dataTime 数据时间
     */
    void sendMessage(Long taskId, String code, Date dataTime);
}
