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

package com.treefinance.saas.taskcenter.service.param;

import com.treefinance.saas.taskcenter.common.enums.ETaskStep;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * Good Luck Bro , No Bug !
 *
 * @author haojiahong
 * @date 2018/6/3
 */
@Getter
@Setter
@ToString
public class TaskStepLogObject implements Serializable {

    /**
     * @see ETaskStep#getText()
     */
    private String stepMsg;
    private String errorMsg;
    private Date occurTime;

    public TaskStepLogObject() {}

    public TaskStepLogObject(String stepMsg, String errorMsg, Date occurTime) {
        this.stepMsg = stepMsg;
        this.errorMsg = errorMsg;
        this.occurTime = occurTime;
    }
}
