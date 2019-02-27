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

package com.treefinance.saas.taskcenter.dto;

import java.io.Serializable;

/**
 * Good Luck Bro , No Bug !
 *
 * @author haojiahong
 * @date 2018/6/11
 */
public class CallbackFailureReasonDTO implements Serializable {

    private static final long serialVersionUID = -3496927105989144684L;

    private Long taskId;

    private Long callbackConfigId;

    private Byte failureReason;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Byte getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(Byte failureReason) {
        this.failureReason = failureReason;
    }

    public Long getCallbackConfigId() {
        return callbackConfigId;
    }

    public void setCallbackConfigId(Long callbackConfigId) {
        this.callbackConfigId = callbackConfigId;
    }
}
