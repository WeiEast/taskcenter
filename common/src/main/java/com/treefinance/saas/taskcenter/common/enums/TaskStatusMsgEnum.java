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

package com.treefinance.saas.taskcenter.common.enums;

/**
 * @author Jerry
 * @date 2018/11/21 00:56
 */
public enum TaskStatusMsgEnum {
    CREATE_MSG("任务创建"), SUCCESS_MSG("任务成功"), FAILURE_MSG("任务失败"), TIMEOUT_MSG("任务超时"), CANCEL_MSG("任务取消");

    private String text;

    TaskStatusMsgEnum(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
