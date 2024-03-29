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

package com.treefinance.saas.taskcenter.dto.moxie;

import java.io.Serializable;

/**
 * Created by haojiahong on 2017/9/21.
 */
public class MoxieTaskEventNoticeMessage implements Serializable {
    private static final long serialVersionUID = 3646957264685997957L;

    /**
     * 魔蝎回调通知对应的魔蝎任务id
     */
    private String moxieTaskId;
    /**
     * 魔蝎回调通知对应的回调信息
     */
    private String message;

    public String getMoxieTaskId() {
        return moxieTaskId;
    }

    public void setMoxieTaskId(String moxieTaskId) {
        this.moxieTaskId = moxieTaskId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
