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

package com.treefinance.saas.taskcenter.common.enums;

/**
 * 任务属性
 * 
 * @author yh-treefinance
 * @date 2017/11/1.
 */
public enum ETaskAttribute {
    /**
     * 魔蝎任务Id
     */
    FUND_MOXIE_TASKID("moxie-taskId", "魔蝎任务Id"),
    /**
     * 魔蝎任务验证码输入次数
     */
    FUND_MOXIE_VERIFY_CODE_COUNT("moxieVerifyCodeCount", "魔蝎任务验证码输入次数"),
    /**
     * 运营商分组编码
     */
    OPERATOR_GROUP_CODE("groupCode", "运营商编码"),
    /**
     * 运营商分组名称
     */
    OPERATOR_GROUP_NAME("groupName", "运营商名称"),
    /**
     * 用户手机号
     */
    MOBILE("mobile", "手机号"),
    /**
     * 用户姓名
     */
    NAME("name", "姓名"),
    /**
     * 身份证号
     */
    ID_CARD("idCard", "身份证号"),
    /**
     * 任务来源类型
     */
    SOURCE_TYPE("sourceType", "请求来源"),
    /**
     * 登录时间
     */
    LOGIN_TIME("loginTime", "登录时间"),
    /**
     * 任务最近活动时间
     */
    ALIVE_TIME("aliveTime", "活跃时间"),
    /**
     * 任务来源ID
     */
    SOURCE_ID("sourceId", "APP区分");

    private final String value;
    private final String desc;

    ETaskAttribute(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public String value() {
        return value;
    }

    public String getAttribute() {
        return value();
    }

    public String getDesc() {
        return desc;
    }
}
