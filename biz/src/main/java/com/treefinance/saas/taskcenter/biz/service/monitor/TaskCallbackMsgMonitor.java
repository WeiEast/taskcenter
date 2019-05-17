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

import com.treefinance.saas.taskcenter.service.domain.TaskInfo;
import com.treefinance.saas.taskcenter.service.param.CallbackRecordObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Jerry
 * @date 2019-05-17 16:48
 */
public interface TaskCallbackMsgMonitor {

    /**
     * 发送消息
     *
     * @param task 任务
     * @param callbackRecord 回调记录
     */
    void sendMessage(@Nonnull TaskInfo task, @Nullable CallbackRecordObject callbackRecord);
}
