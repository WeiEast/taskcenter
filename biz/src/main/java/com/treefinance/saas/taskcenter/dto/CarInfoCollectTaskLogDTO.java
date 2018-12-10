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

package com.treefinance.saas.taskcenter.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * Good Luck Bro , No Bug !
 *
 * @author haojiahong
 * @date 2018/6/3
 */
public class CarInfoCollectTaskLogDTO implements Serializable {
    private static final long serialVersionUID = -3558126448590095421L;
    private String msg;
    private Date occurTime;
    private String errorMsg;


    public CarInfoCollectTaskLogDTO() {

    }

    public CarInfoCollectTaskLogDTO(String msg, String errorMsg, Date occurTime) {
        this.msg = msg;
        this.errorMsg = errorMsg;
        this.occurTime = occurTime;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Date getOccurTime() {
        return occurTime;
    }

    public void setOccurTime(Date occurTime) {
        this.occurTime = occurTime;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
