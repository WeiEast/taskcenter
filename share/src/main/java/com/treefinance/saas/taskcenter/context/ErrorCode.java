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

package com.treefinance.saas.taskcenter.context;

/**
 * @author Jerry
 * @date 2018/12/13 19:53
 */
public enum ErrorCode {

    /**
     * 内部服务异常，默认异常
     */
    INTERNAL_SERVER_ERROR("internal_server_error", "内部服务异常!"),
    /**
     * 非法参数
     */
    INVALID_PARAMETER("invalid_parameter", "非法请求参数!"),
    /**
     * 业务数据异常
     */
    ILLEGAL_BUSINESS_DATA("illegal_business_data", "异常业务数据!");

    private String code;
    private String desc;

    ErrorCode(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
