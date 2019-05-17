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

package com.treefinance.saas.taskcenter.service.param;

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.taskcenter.interation.manager.domain.CallbackConfigBO;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author Jerry
 * @date 2019-05-17 13:38
 */
@Getter
@Setter
public class CallbackRecordObject implements Serializable {

    /**
     * 回调类型
     */
    private Byte type;
    /**
     * 回调请求参数，json格式
     */
    private String requestParameters;
    /**
     * 回调配置
     */
    private CallbackConfigBO config;
    /**
     * 回调响应状态码
     */
    private int responseStatusCode;
    /**
     * 回调响应数据
     */
    private String responseData;
    /**
     * 回调结果解析
     */
    private CallbackResult callbackResult;
    /**
     * 回调错误
     */
    private Throwable exception;
    /**
     * 耗时
     */
    private long cost;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    public static class CallbackResult implements Serializable {
        private String code;
        private String errorMsg;

        public CallbackResult() {}

        public CallbackResult(String code, String errorMsg) {
            this.code = code;
            this.errorMsg = errorMsg;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getErrorMsg() {
            return errorMsg;
        }

        public void setErrorMsg(String errorMsg) {
            this.errorMsg = errorMsg;
        }

        @Override
        public String toString() {
            return JSON.toJSONString(this);
        }
    }
}
