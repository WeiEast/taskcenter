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

package com.treefinance.saas.taskcenter.biz.service.directive.process;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * @author Jerry
 * @date 2019-05-17 11:17
 */
public class CallbackResponse implements Serializable {
    private static final Logger LOGGER = LoggerFactory.getLogger(CallbackResponse.class);
    private final int statusCode;
    private final String data;
    private final Throwable exception;

    private CallbackData result;

    public CallbackResponse(String data) {
        this(200, data, null);
    }

    public CallbackResponse(int statusCode, String data, Throwable exception) {
        this.statusCode = statusCode;
        this.data = data;
        this.exception = exception;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getData() {
        return data;
    }

    public Throwable getException() {
        return exception;
    }

    public CallbackData getResult() {
        if (result == null) {
            try {
                String result = StringUtils.trimToEmpty(this.data);
                if (StringUtils.isNotEmpty(result) && result.startsWith("{") && result.endsWith("}")) {
                    LOGGER.info("parsing callback response data >> {}", this.data);
                    this.result = JSON.parseObject(result, CallbackData.class);
                }
            } catch (Exception e) {
                LOGGER.error("parsing callback response data failed >> {}", this.data, e);
                this.result = new CallbackData("回调响应数据解析失败: " + this.data);
            }
        }

        return result;
    }

    public static final class CallbackData {

        /**
         * 兼容老的返回值
         */
        private boolean success = true;
        private String code;
        private String errorMsg;
        private T data;

        public CallbackData() {}

        public CallbackData(String errorMsg) {
            this.errorMsg = errorMsg;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
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

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }
    }
}
